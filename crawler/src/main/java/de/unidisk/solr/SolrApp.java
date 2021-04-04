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

/**
 * Created by carl on 06.01.16.
 */
public class SolrApp {
    static private final Logger logger = LogManager.getLogger(SolrApp.class.getName());
    private IProjectRepository projectRepository;
    private IScoringService scoringService;
    private IResultService resultService;
    private ProjectGenerationService projectGenerationService;

    public SolrApp(IProjectRepository projectRepository, IScoringService scoringService,
                   IResultService resultService, ProjectGenerationService projectGenerationService) {
        this.projectRepository = projectRepository;
        this.scoringService = scoringService;
        this.resultService = resultService;
        this.projectGenerationService = projectGenerationService;
    }

    void evaluateProject(Project project) {
        logger.info("Start processing project " + project.getName() + " .");
        try {
            projectRepository.updateProjectState(project.getId(), ProjectState.RUNNING);
            List<Project> generatedProjects = null;

            final boolean isParentProject = !project.isSubproject();
            final boolean subprojectsGenerated = project.getSubprojects().size() == ProjectSubtype.values().length - 1;
            if(isParentProject && !subprojectsGenerated){
                logger.debug("Generate subprojects");
                generatedProjects = projectGenerationService.generateSubprojects(String.valueOf(project.getId()));
            }

            logger.debug("Entering main");

            final List<Topic> topics = project.getTopics();
            for(Topic t : topics){
                for(Keyword keyword : t.getKeywords()){
                    final List<ScoreResult> scores = this.scoringService.getKeywordScore(project.getId(),keyword.getId());
                    for(ScoreResult score : scores) {
                        logger.info("Keyword " + score.getEntityId() + ": " + score.getScore());
                        this.resultService.createKeywordScore(score);
                    }
                }
                final List<ScoreResult> topicScores = this.scoringService.getTopicScores(project.getId(), t.getId());
                for(ScoreResult score : topicScores) {
                    this.resultService.createTopicScore(score);
                }
            }
            logger.info("finished processing project " + project.getName() + " .");
            projectRepository.updateProjectState(project.getId(),ProjectState.FINISHED);
            /* Immediately evaluate generated projects so user won't have to wait for next evaluation run.
               Only want to do this when the subprojects have been generated now as otherwise they are
               part of pendingProjects in [execute] and will be evaluated twice.
             */
            if(generatedProjects != null){
                generatedProjects.forEach(this::evaluateProject);
            }
        } catch (Exception e) {
            if(e instanceof EntityNotFoundException){
                projectRepository.setProjectError(project.getId(), "Ein Stichwort oder Thema konnte nicht gefunden werden.");
            }else if(e instanceof MalformedURLException){
                projectRepository.setProjectError(project.getId(), "Die Webseite einer Universität konnte nicht gefunden werden.");
            }else{
                projectRepository.setProjectError(project.getId(), e.getLocalizedMessage());
            }
            logger.error("Error occured while processing project " + project.getName() + " with id " + project.getId() + " .");
            logger.error(e);

            projectRepository.updateProjectState(project.getId(),ProjectState.ERROR);

            e.printStackTrace();

        }
    }

    public void execute() {

        final List<Project> pendingProjects = projectRepository.getProjects(ProjectState.WAITING);

        if(pendingProjects.isEmpty()){
            logger.info("No projects waiting for processing.");
            return;
        }

        for(Project project : pendingProjects) {
            evaluateProject(project);
        }
    }

    public static void main(String[] params) throws Exception {
        final IKeywordRepository keywordRepository = new HibernateKeywordRepo();
        final ITopicRepository topicRepository = new HibernateTopicRepo();

        final IScoringService scoringService = new SolrScoringService(keywordRepository,
                topicRepository,
                SolrConfiguration.getInstance());
        final IProjectRepository projectRepository = new ProjectDAO();
        final IResultService resultService = new HibernateResultService();
        final ProjectGenerationService projectGenerationService = new ProjectGenerationService(
                projectRepository,
                topicRepository,
                keywordRepository,
                new KeywordRecommendationService()
        );

        SolrApp sapp = new SolrApp(projectRepository,scoringService,resultService,projectGenerationService);
        sapp.execute();
    }
}
