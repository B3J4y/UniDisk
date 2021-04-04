package de.unidisk.solr;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.entities.hibernate.*;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.services.KeywordRecommendationService;
import de.unidisk.services.ProjectGenerationService;
import de.unidisk.solr.services.SolrScoringService;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.services.HibernateResultService;
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

    public void execute() throws Exception {

        final List<Project> pendingProjects = projectRepository.getProjects(ProjectState.WAITING);

        if(pendingProjects.isEmpty()){
            logger.info("No projects waiting for processing.");
            return;
        }

        for(Project p : pendingProjects) {
            logger.info("Start processing project " + p.getName() + " .");
            projectRepository.updateProjectState(p.getId(), ProjectState.RUNNING);

            final boolean isParentProject = !p.isSubproject();
            final boolean subprojectsGenerated = p.getSubprojects().size() == ProjectSubtype.values().length - 1;
            if(isParentProject && !subprojectsGenerated){
                logger.debug("Generate subprojects");
                projectGenerationService.generateSubprojects(String.valueOf(p.getId()));
            }

            logger.debug("Entering main");

            try {
                final List<Topic> topics = p.getTopics();
                for(Topic t : topics){
                    for(Keyword keyword : t.getKeywords()){
                        final List<ScoreResult> scores = this.scoringService.getKeywordScore(p.getId(),keyword.getId());
                        for(ScoreResult score : scores) {
                            logger.info("Keyword " + score.getEntityId() + ": " + score.getScore());
                            this.resultService.createKeywordScore(score);
                        }
                    }
                    final List<ScoreResult> topicScores = this.scoringService.getTopicScores(p.getId(), t.getId());
                    for(ScoreResult score : topicScores) {

                        this.resultService.createTopicScore(score);
                    }
                }
                logger.info("finished processing project " + p.getName() + " .");
                projectRepository.updateProjectState(p.getId(),ProjectState.FINISHED);

            } catch (Exception e) {
                if(e instanceof EntityNotFoundException){
                    projectRepository.setProjectError(p.getId(), "Ein Stichwort oder Thema konnte nicht gefunden werden.");
                }else if(e instanceof MalformedURLException){
                    projectRepository.setProjectError(p.getId(), "Die Webseite einer Universtität konnte nicht gefunden werden.");
                }else{
                    projectRepository.setProjectError(p.getId(), e.getLocalizedMessage());
                }
                logger.error("Error occured while processing project " + p.getName() + " .");
                logger.error(e);

                projectRepository.updateProjectState(p.getId(),ProjectState.ERROR);

                e.printStackTrace();

            }
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
