package de.unidisk.solr;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.UniversityDAO;
import de.unidisk.entities.hibernate.*;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.services.HibernateResultService;
import de.unidisk.services.KeywordRecommendationService;
import de.unidisk.services.ProjectGenerationService;
import de.unidisk.solr.services.SolrScoringService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by carl on 06.01.16.
 */
public class SolrApp {
    static private final Logger logger = LogManager.getLogger(SolrApp.class.getName());
    private IProjectRepository projectRepository;
    private IScoringService scoringService;
    private IResultService resultService;
    private ProjectGenerationService projectGenerationService;
    private IKeywordRepository keywordRepository;
    private ITopicRepository topicRepository;

    private static Lock lock = new ReentrantLock();

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public SolrApp(IProjectRepository projectRepository, IScoringService scoringService,
                   IResultService resultService, ProjectGenerationService projectGenerationService,
                   IKeywordRepository keywordRepository, ITopicRepository topicRepository) {
        this.projectRepository = projectRepository;
        this.scoringService = scoringService;
        this.resultService = resultService;
        this.projectGenerationService = projectGenerationService;
        this.keywordRepository = keywordRepository;
        this.topicRepository = topicRepository;
    }

    void evaluateProject(Project project) {

        Timer heartbeatTimer = new Timer( );

      logInfo("Start processing project " + project.getName() + " with id " + project.getId() +" of type "  + project.getProjectSubtype() + ".");
      long evaluationStart = System.currentTimeMillis();
      try {
          // dead projects already have the state set to running
          if(project.getProjectState() != ProjectState.RUNNING)
            projectRepository.updateProjectState(project.getId(), ProjectState.RUNNING);
          heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
              @Override
              public void run() {
                  try {
                      projectRepository.updateHeartbeat(String.valueOf(project.getId()));
                      logInfo("Updated heartbeat of project " + project.getId());
                  } catch (EntityNotFoundException e) {
                      e.printStackTrace();
                  }
              }
          }, 5000,5000);
        List<Project> generatedProjects = null;

        final boolean isParentProject = !project.isSubproject();
        final boolean subprojectsGenerated = project.getSubprojects().size() == ProjectSubtype.values().length - 1;
        if(isParentProject && !subprojectsGenerated){
            logInfo("Generate subprojects");
            generatedProjects = projectGenerationService.generateSubprojects(String.valueOf(project.getId()));
        }

        logInfo("Entering main");

        final List<Topic> topics = project.getTopics().stream().filter(topic -> !topic.finishedProcessing()).collect(Collectors.toList());
        logInfo("Process " + topics.size() + " topics");
        for(Topic t : topics){
            long topicEvaluationStart = System.currentTimeMillis();
            logInfo("Process " + t.getKeywords().size() + " keywords for topic " + t.getName());
            Stream<Keyword> unfinishedKeywords = t.getKeywords().stream().filter(keyword -> !keyword.finishedProcessing());

            List<Future<Object>> keywordsFutures = unfinishedKeywords.map(keyword ->
                 executorService.submit(() -> {
                    long keywordEvaluationStart = System.currentTimeMillis();
                    final List<ScoreResult> scores = this.scoringService.getKeywordScores(project.getId(),keyword.getId());
                    if(scores.isEmpty()){
                        logInfo("No keyword scores found for keyword " + keyword.getName());
                    }else {
                        // Filter scores to reduce processing time
                        final List<ScoreResult> relevantScores = this.scoringService.filterRelevantKeywordScores(scores);
                        logInfo("Create " + relevantScores.size() + " keyword scores for " +keyword.getName());
                        try{
                            this.resultService.createKeywordScores(relevantScores);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                    keywordRepository.finishedProcessing(keyword.getId());

                    long keywordEvaluationTime = System.currentTimeMillis() - keywordEvaluationStart;
                    logInfo("Evaluating keyword " + keyword.getName() + "("+keyword.getId()+") took " + (keywordEvaluationTime/1000.0) + " seconds");

                    return null;
                })
            ).collect(Collectors.toList());

            for(Future<Object> obj : keywordsFutures){
                obj.get();
            }

            final List<ScoreResult> topicScores = this.scoringService.getTopicScores(project.getId(), t.getId());
            for(ScoreResult score : topicScores) {
                this.resultService.createTopicScore(score);
            }
            long topicEvaluationTime = System.currentTimeMillis() - topicEvaluationStart;
            topicRepository.finishedProcessing(t.getId());
            logInfo("Evaluating topic " + t.getName() + "("+t.getId()+") took " + (topicEvaluationTime/1000.0) + " seconds");
        }
        logInfo("finished processing project " + project.getName() + " .");
        projectRepository.updateProjectState(project.getId(),ProjectState.FINISHED);
        // Cancel call is idempotent so multiple invocations are safe
        heartbeatTimer.cancel();
        /* Immediately evaluate generated projects so user won't have to wait for next evaluation run.
           Only want to do this when the subprojects have been generated now as otherwise they are
           part of pendingProjects in [execute] and will be evaluated twice.
         */
        long evaluationTime = System.currentTimeMillis() - evaluationStart;
        logInfo("Evaluating project " + project.getId() + " took " + (evaluationTime/1000.0) + " seconds");
        if(generatedProjects != null){
            generatedProjects.forEach(this::evaluateProject);
        }
    } catch (Exception e) {

        e.printStackTrace();
        if(e instanceof EntityNotFoundException){
            projectRepository.setProjectError(project.getId(), "Ein Stichwort oder Thema konnte nicht gefunden werden.");
        }else if(e instanceof MalformedURLException){
            projectRepository.setProjectError(project.getId(), "Die Webseite einer Universität konnte nicht gefunden werden.");
        }else{
            projectRepository.setProjectError(project.getId(), e.getLocalizedMessage());
        }
        final String errorMessage  = "Error occured while processing project " + project.getName() + " with id " + project.getId() + " .";
        logger.error(errorMessage);
        logger.error(e);

        projectRepository.updateProjectState(project.getId(),ProjectState.ERROR);


    }
      finally {
          heartbeatTimer.cancel();
      }
    }

    // Placeholder until someone can figure out how log4j works
    void logInfo(String message){
        System.out.println(message);
    }

    public void execute() {
        lock.lock();

        try {
            if(!scoringService.canEvaluate()){
                logInfo("Currently unable to score keywords.");
                return;
            }

            if(!projectGenerationService.isAvailable()){
                logInfo("Project generation service not available.");
                return;
            }

            final List<Project> pendingProjects = projectRepository.getDeadProjects();
            if(!pendingProjects.isEmpty()){
                logInfo("Retry " + pendingProjects.size() + " dead projects.");
                projectRepository.cleanDeadProjects();
            }
            pendingProjects.addAll(projectRepository.getProjects(ProjectState.WAITING));

            if (pendingProjects.isEmpty()) {
                logInfo("No projects waiting for processing.");
                return;
            }

            logInfo("Start processing " + pendingProjects.size() + " projects.");

            for (Project project : pendingProjects) {
                evaluateProject(project);
            }

            logInfo("Finished processing projects.");
        }
        finally {
            lock.unlock();
        }
    }

    public static void main(String[] params) throws Exception {
        final IKeywordRepository keywordRepository = new HibernateKeywordRepo();
        final ITopicRepository topicRepository = new HibernateTopicRepo();

        final IScoringService scoringService = new SolrScoringService(keywordRepository,
                topicRepository,
                SolrConfiguration.getInstance(),
                new UniversityDAO()
        );
        final IProjectRepository projectRepository = new ProjectDAO();
        final IResultService resultService = new HibernateResultService();
        final ProjectGenerationService projectGenerationService = new ProjectGenerationService(
                projectRepository,
                topicRepository,
                keywordRepository,
                new KeywordRecommendationService()
        );

        SolrApp sapp = new SolrApp(projectRepository,scoringService,resultService
                ,projectGenerationService,keywordRepository, topicRepository);
        sapp.execute();
    }
}
