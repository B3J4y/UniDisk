package de.unidisk.crawler.rest;

import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.crawler.rest.dto.project.CreateProjectDto;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/project")
public class ProjectServiceRest {

    private IProjectRepository projectRepository = new ProjectDAO();


    @GET
    @Path("all")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response projects(){
        Project p = new Project();
        p.setId(5);
        p.setName("Test");
        return Response.ok(Collections.singletonList(p)).build();
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response project(@PathParam("id") String id){
        Optional<Project> project = this.projectRepository.getProject(id);

        if(!project.isPresent()){
            return Response.status(404).build();
        }

        return Response.ok(project.get()).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProject(CreateProjectDto dto){
        return Response.ok().build();
    }


    @PUT
    @Produces({ MediaType.APPLICATION_JSON})
    public Response updateProject(){
        this.projectRepository.
        return Response.ok().build();
    }



    @DELETE
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response deleteProject(@PathParam("id") int id){
        return Response.ok().build();
    }
}
