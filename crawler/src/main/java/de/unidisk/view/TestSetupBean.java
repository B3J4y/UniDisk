package de.unidisk.view;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.common.exceptions.SeedData;
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
import de.unidisk.dao.UniversityDAO;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.University;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.services.HibernateResultService;
import de.unidisk.services.KeywordRecommendationService;
import de.unidisk.services.ProjectGenerationService;
import de.unidisk.solr.SolrApp;
import de.unidisk.solr.SolrConnector;
import de.unidisk.solr.services.SolrScoringService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Klasse initialisiert die Datenbank mit Testdaten.
 * Dies wird beim Start der Anwendung ausgeführt.
 */

@Provider
public class TestSetupBean {

    static private final Logger logger = LogManager.getLogger(TestSetupBean.class.getName());

    private UniCrawlService uniCrawlService;

    private SolrApp solrApp;

    @Inject
    private IUniversityRepository universityRepository;

    @Inject
    private IProjectRepository projectRepository;

    @Inject
    private IKeywordRepository keywordRepository;

    @Inject
    private ITopicRepository topicRepository;

    @Inject
    private IResultService resultService;

    public TestSetupBean() {
    }

    public static TestSetupBean Default(){
        final TestSetupBean bean = new TestSetupBean();
        bean.setProjectRepository(new HibernateProjectRepo());
        bean.setKeywordRepository(new HibernateKeywordRepo());
        bean.setTopicRepository(new HibernateTopicRepo());
        bean.setResultService(new HibernateResultService());
        bean.setUniversityRepository(new UniversityDAO());
        return bean;
    }

    private Timer crawlTimer,scoringTimer;

    public void init() throws IOException, SolrServerException {
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
        createViews();
        initCore();
        seed();
        if(!config.getCrawlerConfiguration().isDisabled())
        setupCrawlJob();
        setupScoringJob();
    }

    private void createViews() throws IOException {
        final String[] sqlViewFiles = new String[]{
                "SearchMetaDataEval.sql"
        };
        for (String sqlViewFile : sqlViewFiles) {
            final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sql/views/" + sqlViewFile);
            String content = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            HibernateUtil.executeVoid(session -> {
                session.createSQLQuery(content).executeUpdate();
            });
        }
    }

    private void seed() throws IOException {
        final List<University> existingUniversities = universityRepository.getUniversities();
        final Set<String> existingNames = existingUniversities.stream().map(University::getName).collect(Collectors.toSet());
        final List<University> universitySeeds = SeedData.getSeedUniversities();

        final List<University> newUniversities = universitySeeds.stream().filter(university -> !existingNames.contains(university.getName())).collect(Collectors.toList());
        if(newUniversities.isEmpty())
            return;
        logInfo("Create " + newUniversities.size() + " new universities");
        for (University newUniversity : newUniversities) {
            universityRepository.create(newUniversity);
        }
    }


    private boolean coreExists(SolrClient client, String coreName) throws IOException, SolrServerException {
        CoreAdminRequest request = new CoreAdminRequest();
        request.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        CoreAdminResponse cores = request.process(client);
        for (int i = 0; i < cores.getCoreStatus().size(); i++) {
            String core = cores.getCoreStatus().getName(i);
            if(core.equals(coreName)){
                return true;
            }
        }
        return false;
    }

    public void initCore() throws IOException, SolrServerException {
        SolrConfiguration solrConfiguration = SystemConfiguration.getInstance().getSolrConfiguration();
        SolrClient client = new SolrConnector(solrConfiguration).getServerClient();
        String coreName = solrConfiguration.getCore();

        if(coreExists(client,coreName)){
            logInfo("Core already exists.");
            return;
        }
        throw new RuntimeException("Solr core does not exist.");
    }

    private void logInfo(String info){
        System.out.println(info);
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
        final IScoringService scoringService = new SolrScoringService(keywordRepository,topicRepository,
                SolrConfiguration.getInstance(), universityRepository);
        final ProjectGenerationService projectGenerationService = new ProjectGenerationService(
                projectRepository,
                topicRepository,
                keywordRepository,
                new KeywordRecommendationService()
        );
        solrApp = new SolrApp(projectRepository,scoringService,resultService
                ,projectGenerationService,keywordRepository, topicRepository
        );

        final long scoringInterval = SystemConfiguration.getInstance().getScoringInterval();
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
        } , 0,scoringInterval);
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