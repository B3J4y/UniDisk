package de.unidisk.crawler.rest;

import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.crawler.rest.authentication.ContextUser;
import de.unidisk.crawler.rest.dto.topic.CreateTopicDto;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/topic")
public class TopicRestService extends CRUDService<Topic, CreateTopicDto> {

    @Inject
    ITopicRepository topicRepository;

    @Inject
    IProjectRepository projectRepository;

    @Override
    protected Response executeCreate(CreateTopicDto dto, ContextUser user) {
        final Optional<Project> project = this.projectRepository.getProject(dto.getProjectId());
        if(!project.isPresent()){
            return Response.status(400).entity("Projekt mit ID existiert nicht.").build();
        }
        System.out.println("user id " + user.getId() +" " + project.get().getUserId());
        if(!project.get().getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        final Topic topic = this.topicRepository.createTopic(Integer.parseInt(dto.getProjectId()),dto.getName());
        return Response.ok(topic).build();
    }
}
