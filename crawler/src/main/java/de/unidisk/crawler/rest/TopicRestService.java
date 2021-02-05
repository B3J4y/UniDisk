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
    protected Response executeCreate(ContextUser user,CreateTopicDto dto) {
        final Optional<Project> project = this.projectRepository.getProject(dto.getProjectId());
        if(!project.isPresent()){
            return Response.status(400).entity("Projekt mit ID existiert nicht.").build();
        }
        if(!project.get().getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        final Topic topic = this.topicRepository.createTopic(Integer.parseInt(dto.getProjectId()),dto.getName());
        return Response.ok(topic).build();
    }

    @Override
    protected Response executeDelete(ContextUser user, String id) {
        final Optional<Topic> optionalTopic = this.topicRepository.getTopic(Integer.parseInt(id));
        if(!optionalTopic.isPresent()){
            return Response.status(404).build();
        }
        final Topic topic = optionalTopic.get();
        final int topicId = topic.getId();
        final Project project = this.projectRepository.getProject(String.valueOf(topic.getProjectId())).get();
        if(!project.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        this.topicRepository.deleteTopic(topicId);
        return Response.ok().build();
    }
}
