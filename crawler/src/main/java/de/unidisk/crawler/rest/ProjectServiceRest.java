package de.unidisk.crawler.rest;

import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.crawler.rest.authentication.AuthNeeded;
import de.unidisk.crawler.rest.authentication.ContextUser;
import de.unidisk.crawler.rest.dto.project.CreateProjectDto;
import de.unidisk.crawler.rest.dto.project.UpdateProjectDto;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.function.Function;

@Path("/project")
public class ProjectServiceRest {

    @Inject
    IProjectRepository projectRepository;

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response projects(@Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        final List<Project> projects = this.projectRepository.getUserProjects(user.getId());
        return Response.ok(projects).build();
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response project(@PathParam("id") String id){
        return runProject(id, project -> Response.ok(project).build());
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response createProject(CreateProjectDto dto, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        final IProjectRepository.CreateProjectArgs args = new IProjectRepository.CreateProjectArgs(user.getId(),dto.getName());
        final Project p = this.projectRepository.createProject(args);
        return Response.ok(p).build();
    }


    @PUT
    @Path("{id}")
    @Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProject(UpdateProjectDto dto, @PathParam("id") String id){
        return runProject(id, project -> {
            final Project updated = this.projectRepository.updateProject(id,dto.getName());
            return Response.ok(updated).build();
        }
        );
    }


    @POST
    @Path("{id}/enqueue")
    public Response enqueueProject(String id){
        return runProject(id, project -> {
            final ProjectState currentState = project.getProjectState();
                    if(currentState == ProjectState.IDLE){
                        this.projectRepository.updateProjectState(Integer.parseInt(id), ProjectState.WAITING);
                        return Response.ok().build();
                    }

                    return Response.status(400).entity("Projekt bereits zur Bearbeitung eingereiht.").build();
                }
        );
    }


    @POST
    @Path("{id}/dequeue")
    public Response dequeueProject(String id){
        return runProject(id, project -> {
                    final ProjectState currentState = project.getProjectState();
                    if(currentState == ProjectState.WAITING || currentState == ProjectState.ERROR){
                        this.projectRepository.updateProjectState(Integer.parseInt(id), ProjectState.IDLE);
                        return Response.ok().build();
                    }

                    return Response.status(400).entity("Projektstatus kann momentan nicht geändert werden.").build();
                }
        );
    }


    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response deleteProject(@PathParam("id") String id){
        return runProject(id, project -> {
            final boolean deleted = this.projectRepository.deleteProject(id);
            return Response.ok(deleted).build();
        });
    }

    /**
     * Executes [task] if project with [id] exists, otherwise returns 404 status.
     * @param id id of project
     * @param task function to be invoked if project exists
     * @return
     */
    private Response runProject(String id, Function<Project, Response> task){
        final Optional<Project> project = this.projectRepository.getProject(id);
        if(!project.isPresent()){
            return Response.status(404).build();
        }
        return task.apply(project.get());
    }
}
