package de.unidisk.crawler;



import de.unidisk.config.CrawlerConfig;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.crawler.model.UniversitySeed;
import de.unidisk.crawler.simple.CrawlConfiguration;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.entities.hibernate.University;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import de.unidisk.repositories.HibernateUniversityRepo;
import de.unidisk.solr.SolrApp;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.solr.SolrConnector;
import de.unidisk.solr.services.SolrScoringService;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.services.IResultService;
import de.unidisk.services.HibernateResultService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;


/**
 * Root resource (exposed at "competences" path)
 */
@Path("/crawler")
public class CrawlerServiceRest {

    //static private List<Thread> threads = new ArrayList<Thread>();
    static private final int MAXTHREAD = 4;
    static private int CURRENTTHREADS = 0;
    static private final Logger logger = LogManager.getLogger(CrawlerServiceRest.class.getName());
    static private ThreadGroup tg = new ThreadGroup("SolrApps");

    // TODO comment in the following and adapt to the current project situation


    private IProjectRepository projectRepository = new ProjectDAO();
    private IScoringService iScoringService = new SolrScoringService(new HibernateKeywordRepo(), new HibernateTopicRepo(), SolrConfiguration.Instance());
    private IResultService iResultService = new HibernateResultService();

    @GET
    @Path("/isRunning")
    public String isRunning() {
        return "Hello Jan!";
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("crawler/university")
    public Response crawl(
            @QueryParam("universityId") int universityId
    ) throws Exception{
        final IUniversityRepository universityRepository = new HibernateUniversityRepo();
        final Optional<University> uni = universityRepository.getUniversity(universityId);
        if(!uni.isPresent()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final  University university = uni.get();

        CURRENTTHREADS ++;
        if (CURRENTTHREADS <=  MAXTHREAD) {
            Thread t = new Thread(tg, (Runnable) () -> {
                logger.debug("Thread is running");
                final SimpleCrawl crawl = new SimpleCrawl(CrawlerConfig.storageLocation,
                        new UniversitySeed(university.getSeedUrl(),university.getId()),
                        SolrConfiguration.getTestUrl(),
                        100
                        );
                try {
                    crawl.startMultipleCrawl();
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                } finally {
                    CURRENTTHREADS --;
                }
                logger.debug("Thread is done");
            });
            //threads.add(t);
            t.start();

            return Response.ok("Start crawling university " + uni.get().getName()).build();
        } else {
            return Response.ok("too many threads. Plz come back later").build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/start")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response start(
            @QueryParam("campaign") String campaign)
            throws Exception {
        //to have access to the variable in the Runnable
        final String campaignOs = campaign;
        CURRENTTHREADS ++;
        if (CURRENTTHREADS <=  MAXTHREAD) {
            Thread t = new Thread(tg, new Runnable() {
                @Override
                public void run() {
                    logger.debug("Thread is running");
                    SolrApp solrApp = new SolrApp(projectRepository, iScoringService, iResultService);
                    try {
                        solrApp.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    } finally {
                        //threads.remove(this);
                        //gc();
                        CURRENTTHREADS --;
                    }
                    logger.debug("Thread is done");
                }
            }, campaignOs);
            //threads.add(t);
            t.start();

            return Response.ok("thread started").build();
        } else {
            return Response.ok("too many threads. Plz come back later").build();

        }
    }

    /*
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/stop")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response stop(
            @QueryParam("campaign") String campaign)
            throws Exception {
        //to have access to the variable in the Runnable
        final String campaignOs = campaign;
        logger.debug("Stop " + campaign);
        Thread[] threads = new Thread[MAXTHREAD + 2];
        tg.enumerate(threads);
        for (Thread t : threads) {
            logger.debug(t.getName() + " from " + threads.length + " Threads");
            if (t.getName().equals(campaign)) {
                MysqlConnector mc = new MysqlConnector();
                if (mc.checkCampaignStatus(campaign) == 4) {
                    return Response.ok("thread is already in closing process").build();
                }
                t.interrupt();

                if (mc.checkCampaignStatus(campaign) == 1) {
                    mc.setCampaignStatus(campaign, 4);
                } else {
                    logger.debug("Campaign status is " + mc.checkCampaignStatus(campaign));
                }
                return Response.ok("thread was closed").build();
            }
        }
        return Response.ok("thread doesn't exists").build();
    }*/
}
