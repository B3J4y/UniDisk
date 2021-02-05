package de.unidisk.crawler.rest;

import de.unidisk.crawler.rest.authentication.AuthNeeded;
import de.unidisk.crawler.rest.authentication.ContextUser;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class CRUDService<TEntity, TCreateDto> {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response create(TCreateDto dto, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        return this.executeCreate(user,dto);
    }



    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response delete(String id, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        return this.executeDelete(user,id);
    }

    protected abstract Response executeCreate(ContextUser user,TCreateDto dto);

    protected abstract Response executeDelete(ContextUser user, String id);

}
