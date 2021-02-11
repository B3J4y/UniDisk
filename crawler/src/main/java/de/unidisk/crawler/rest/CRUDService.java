package de.unidisk.crawler.rest;

import de.unidisk.crawler.rest.authentication.AuthNeeded;
import de.unidisk.crawler.rest.authentication.ContextUser;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Optional;

public abstract class CRUDService<TEntity, TCreateDto,TUpdateDto> {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response create(TCreateDto dto, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        return this.executeCreate(user,dto);
    }


    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response update(TUpdateDto dto, @Context SecurityContext context, @PathParam("id") String id){
        final Optional<TEntity> entity = this.getEntity(id);
        if(!entity.isPresent()){
            return Response.status(404).build();
        }
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        return this.executeUpdate(user,dto,entity.get());
    }



    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response delete(@PathParam("id") String id, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        return this.executeDelete(user,id);
    }

    protected abstract Response executeCreate(ContextUser user,TCreateDto dto);

    protected abstract Response executeUpdate(ContextUser user,TUpdateDto dto, TEntity entity);

    protected abstract Optional<TEntity> getEntity(String entityId);

    protected abstract Response executeDelete(ContextUser user, String id);

}
