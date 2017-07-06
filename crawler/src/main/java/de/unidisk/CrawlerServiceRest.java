package de.unidisk;



import de.unidisk.crawler.SolrApp;
import de.unidisk.crawler.mysql.MysqlConnector;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


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


    @GET
    @Path("/isRunning")
    public String isRunning() {
        return "Hello Jan!";
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
                    SolrApp solrApp = new SolrApp(campaignOs);
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
    }
}
