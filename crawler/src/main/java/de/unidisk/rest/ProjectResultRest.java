package de.unidisk.rest;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.rest.authentication.AuthNeeded;
import de.unidisk.rest.dto.topic.RateTopicResultDto;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/result")
public class ProjectResultRest {

    @Inject
    IProjectRepository projectRepository;

    public ProjectResultRest() {
    }

    public ProjectResultRest(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @POST
    @Path("/topic")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response rate(RateTopicResultDto dto, @Context SecurityContext context) throws EntityNotFoundException {
        try {
            projectRepository.rateResult(dto);
            return Response.ok().build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).build();
        }
    }

}