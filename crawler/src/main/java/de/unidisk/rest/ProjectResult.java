package de.unidisk.rest;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.rest.authentication.AuthNeeded;
import de.unidisk.rest.authentication.ContextUser;
import de.unidisk.rest.dto.project.RateResultDto;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/result")
public class ProjectResult {

    IProjectRepository projectRepository;

    public ProjectResult() {
    }

    public ProjectResult(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response rate(RateResultDto dto, @PathParam("id") String id, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        try {
            projectRepository.rateTopicScore(id,dto.getRelevance());
        } catch (EntityNotFoundException e) {
            return Response.status(404).build();
        }
        return Response.ok().build();
    }

}
