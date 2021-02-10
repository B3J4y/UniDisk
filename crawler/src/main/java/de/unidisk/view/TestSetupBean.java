package de.unidisk.view;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.UniCrawlService;
import de.unidisk.dao.HibernateTestSetup;
import de.unidisk.dao.HibernateUtil;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.solr.SolrApp;
import de.unidisk.solr.services.SolrScoringService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedProperty;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Klasse initialisiert die Datenbank mit Testdaten.
 * Dies wird beim Start der Anwendung ausgeführt.
 */

@ManagedBean(eager=true)
@ApplicationScoped
public class TestSetupBean {

    static private final Logger logger = LogManager.getLogger(TestSetupBean.class.getName());

    private UniCrawlService uniCrawlService;

    private SolrApp solrApp;

    @ManagedProperty("#{universityRepo}")
    private IUniversityRepository universityRepository;

    @ManagedProperty("#{projectRepository}")
    private IProjectRepository projectRepository;

    @ManagedProperty("#{keywordRepository}")
    private IKeywordRepository keywordRepository;

    @ManagedProperty("#{topicRepository}")
    private ITopicRepository topicRepository;

    @ManagedProperty("#{resultService}")
    private IResultService resultService;

    private Timer crawlTimer,scoringTimer;

    @PostConstruct
    public void init() {
        SystemConfiguration config = SystemConfiguration.getInstance();
        if(config.getDatabaseConfiguration().isInitializeMockData()){
            final ApplicationState state = MockData.getMockState();
            try {
                HibernateUtil.truncateTable(Project.class);
            }catch(Exception e){
                //fails for h2 database
            }
            HibernateTestSetup.Setup(state);
        }
        setupCrawlJob();
        setupScoringJob();
    }

    @PreDestroy
    private void dispose(){
        if(crawlTimer != null)
            crawlTimer.cancel();
        if(scoringTimer != null)
            scoringTimer.cancel();
    }

    private void setupCrawlJob(){
        if(crawlTimer != null)
            crawlTimer.cancel();

        crawlTimer = new Timer();
        uniCrawlService = new UniCrawlService(universityRepository);
        SystemConfiguration config = SystemConfiguration.getInstance();
        crawlTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    uniCrawlService.start(config.getCrawlerConfiguration().getUniCrawlInterval());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 0,config.getCrawlerConfiguration().getCrawlInterval());
    }

    private void setupScoringJob(){
        if(scoringTimer != null)
            scoringTimer.cancel();


        scoringTimer = new Timer();
        final IScoringService scoringService = new SolrScoringService(keywordRepository,topicRepository, SolrConfiguration.getInstance());
        solrApp = new SolrApp(projectRepository,scoringService,resultService);
        scoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    logger.info("Start solr app execution");
                    solrApp.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 0,SystemConfiguration.getInstance().getScoringInterval());
    }

    public IUniversityRepository getUniversityRepository() {
        return universityRepository;
    }

    public void setUniversityRepository(IUniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void setKeywordRepository(IKeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public void setTopicRepository(ITopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public void setResultService(IResultService resultService) {
        this.resultService = resultService;
    }
}