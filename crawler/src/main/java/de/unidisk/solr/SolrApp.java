package de.unidisk.solr;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.repositories.HibernateUniversityRepo;
import de.unidisk.solr.services.SolrScoringService;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.services.HibernateResultService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
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

    public SolrApp(IProjectRepository projectRepository, IScoringService scoringService,
                   IResultService resultService) {
        this.projectRepository = projectRepository;
        this.scoringService = scoringService;
        this.resultService = resultService;
    }

    private String projectLockFileName(Project p){
        return p.getId()+"_lock.unidisc";
    }

    public void execute() throws Exception {

        final List<Project> pendingProjects = projectRepository.getProjects(ProjectState.WAITING);

        if(pendingProjects.isEmpty()){
            logger.info("No projects waiting for processing.");
            return;
        }

        for(Project p : pendingProjects) {
            final String lockFile = projectLockFileName(p);

            FileInputStream in = new FileInputStream(lockFile);
            java.nio.channels.FileLock lock = null;
            try {
                lock = in.getChannel().lock();
            }catch(Exception e){
                logger.error("Unable to lock project file.");
                logger.error(e);
                continue;
            }

            logger.info("Start processing project: " + p.getName());
            projectRepository.updateProjectState(p.getId(), ProjectState.RUNNING);

            logger.debug("Entering main");
            logger.info("Read out csv or mysql");
            try {

                final List<Topic> topics = p.getTopics();
                for(Topic t : topics){
                    for(Keyword keyword : t.getKeywords()){
                        final List<ScoreResult> scores = this.scoringService.getKeywordScore(p.getId(),keyword.getId());
                        scores.forEach(score -> {
                            try {
                                this.resultService.CreateKeywordScore(score);
                            } catch (EntityNotFoundException | MalformedURLException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    final ScoreResult topicScore = this.scoringService.getTopicScore(p.getId(), t.getId());
                    this.resultService.CreateTopicScore(topicScore);
                }
                projectRepository.updateProjectState(p.getId(),ProjectState.FINISHED);
            } catch (Exception e) {
                logger.error(e);

                projectRepository.updateProjectState(p.getId(),ProjectState.ERROR);
                e.getStackTrace();

            }finally{
                lock.close();
            }
        }

    }
    public static void main(String[] args) throws Exception {
        final IScoringService scoringService = new SolrScoringService(new HibernateKeywordRepo(), new HibernateTopicRepo(), SolrConfiguration.getInstance());
        final IProjectRepository projectRepository = new ProjectDAO();
        final IResultService resultService = new HibernateResultService();

        SolrApp sapp = new SolrApp(projectRepository,scoringService,resultService);
        sapp.execute();
    }
}
