package de.unidisk.crawler;

import de.unidisk.common.SystemProperties;
import de.unidisk.crawler.analysis.CrawlerDataAnalysis;
import de.unidisk.crawler.contracts.IScoringService;
import de.unidisk.crawler.datatype.Model;
import de.unidisk.crawler.io.ReadMysql;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.crawler.services.SolrScoringService;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.contracts.IProjectRepository;
import de.unidisk.repositories.contracts.IResultService;
import de.unidisk.services.HibernateResultService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

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
        return p.getId()+"lock.unidisk";
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
                        final ScoreResult score = this.scoringService.getKeywordScore(p.getId(),keyword.getId());
                        this.resultService.CreateKeywordScore(score);
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
        final IScoringService scoringService = new SolrScoringService();
        final IProjectRepository projectRepository = new ProjectDAO();
        final IResultService resultService = new HibernateResultService();

        SolrApp sapp = new SolrApp(projectRepository,scoringService,resultService);
        sapp.execute();
    }
}
