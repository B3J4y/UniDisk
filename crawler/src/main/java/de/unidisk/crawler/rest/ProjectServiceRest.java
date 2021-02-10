package de.unidisk.crawler.rest;

import de.unidisk.contracts.exceptions.DuplicateException;
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
import java.security.Principal;
import java.util.*;
import java.util.function.Function;

@Path("/project")
public class ProjectServiceRest {

    @Inject
    IProjectRepository projectRepository;

    public ProjectServiceRest(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectServiceRest() {
    }

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
    @AuthNeeded
    public Response project(@PathParam("id") String id,@Context SecurityContext context){
        final Optional<Project> project = this.projectRepository.getProjectDetails(id);
        if(!project.isPresent()){
            return Response.status(404).build();
        }
        final Project p = project.get();
        if(!p.getUserId().equals(getContextUserId(context))){
            return Response.status(Response.Status.FORBIDDEN).entity("Nur der Projektbesitzer hat Zugriff auf das Projekt.").build();
        }
        return Response.ok(p).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response createProject(CreateProjectDto dto, @Context SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        final IProjectRepository.CreateProjectArgs args = new IProjectRepository.CreateProjectArgs(user.getId(),dto.getName());

        try {
            final Project  p = this.projectRepository.createProject(args);
            return Response.ok(p).build();
        } catch (DuplicateException e) {
            return Response.status(400).entity("Projekt mit Namen existiert bereits.").build();
        }
    }


    @PUT
    @Path("{id}")
    @Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response updateProject(UpdateProjectDto dto, @PathParam("id") String id, @Context SecurityContext context ){
        return runProject(id,context, project -> {
            final IProjectRepository.UpdateProjectArgs args = new IProjectRepository.UpdateProjectArgs(id,dto.getName());
                    try {
                        final Project updated = this.projectRepository.updateProject(args);
                        return Response.ok(updated).build();
                    } catch (DuplicateException e) {
                        return Response.status(400).entity("Projekt mit Namen existiert bereits.").build();
                    }
            }
        );
    }


    @POST
    @Path("{id}/enqueue")
    @AuthNeeded
    public Response enqueueProject(String id,@Context SecurityContext context ){
        return runProject(id, context, project -> {
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
    @AuthNeeded
    public Response dequeueProject(String id,@Context SecurityContext context ){
        return runProject(id, context, project -> {
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
    @Produces(MediaType.APPLICATION_JSON)
    @AuthNeeded
    public Response deleteProject(@PathParam("id") String id, @Context SecurityContext context ){
        return runProject(id,context, project -> {
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
    private Response runProject(String id, SecurityContext context, Function<Project, Response> task){
        final Optional<Project> project = this.projectRepository.getProject(id);
        if(!project.isPresent()){
            return Response.status(404).build();
        }
        final Project p = project.get();
        if(!p.getUserId().equals(getContextUserId(context))){
            return Response.status(Response.Status.FORBIDDEN).entity("Nur der Projektbesitzer darf auf das Projekt zugreifen.").build();
        }
        return task.apply(project.get());
    }

    private String getContextUserId(SecurityContext context){
        final ContextUser user = (ContextUser) context.getUserPrincipal();
        return user.getId();
    }
}
