package de.unidisk.rest;
import javax.ws.rs.*;


@Path("/crawler")
public class CrawlerServiceRest {

    @GET
    public String isRunning() {
        return "Hello Jan!";
    }
  
}
