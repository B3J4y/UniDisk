package de.unidisk.view;

import de.unidisk.common.ApplicationState;
import de.unidisk.common.MockData;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.crawler.main.UniCrawlService;
import de.unidisk.entities.hibernate.HibernateTestSetup;
import de.unidisk.solr.SolrApp;

import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedProperty;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Klasse initialisiert die Datenbank mit Testdaten.
 * Dies wird beim Start der Anwendung ausgeführt.
 */

@ManagedBean(eager=true)
@ApplicationScoped
public class TestSetupBean {

    private UniCrawlService uniCrawlService;

    private SolrApp solrApp;

    @ManagedProperty("#{universityRepo}")
    private IUniversityRepository universityRepository;

    @PostConstruct
    public void init() {
        SystemConfiguration config = SystemConfiguration.getInstance();
        if(config.getDatabaseConfiguration().isInitializeMockData()){
            final ApplicationState state = MockData.getMockState();
            HibernateTestSetup.Setup(state);
        }

        uniCrawlService = new UniCrawlService(universityRepository);
        new Thread(() -> {
            uniCrawlService.start();
        });

        Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    solrApp.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 0,60000);
    }

    public IUniversityRepository getUniversityRepository() {
        return universityRepository;
    }

    public void setUniversityRepository(IUniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }
}