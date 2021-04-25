package de.unidisk.rest;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.topic.UpdateTopicParams;
import de.unidisk.rest.authentication.ContextUser;
import de.unidisk.rest.dto.topic.CreateTopicDto;
import de.unidisk.rest.dto.topic.UpdateTopicDto;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/topic")
public class TopicRestService extends CRUDService<Topic, CreateTopicDto, UpdateTopicDto> {

    @Inject
    ITopicRepository topicRepository;

    @Inject
    IProjectRepository projectRepository;

    public TopicRestService(){}

    public TopicRestService(IProjectRepository projectRepository,ITopicRepository topicRepository) {
        this.topicRepository = topicRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    protected Response executeCreate(ContextUser user,CreateTopicDto dto) {
        final Optional<Project> project = this.projectRepository.getProject(dto.getProjectId());
        if(!project.isPresent()){
            return Response.status(400).entity("Projekt mit ID existiert nicht.").build();
        }
        final Project p = project.get();
        if(!p.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if(!p.canEdit()){
            return Response.status(400).entity("Projekt kann im aktuellen Status nicht bearbeitet werden.").build();
        }


        final Topic topic;
        try {
            topic = this.topicRepository.createTopic(Integer.parseInt(dto.getProjectId()),dto.getName());
        } catch (DuplicateException e) {
            return Response.status(400).entity("Thema mit gleichem Namen existiert bereits.").build();
        }
        return Response.ok(topic).build();
    }

    @Override
    protected Response executeUpdate(ContextUser user, UpdateTopicDto updateTopicDto, Topic topic) {
        final Project project = this.projectRepository.getProject(String.valueOf(topic.getProjectId())).get();
        if(!project.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try {
            final UpdateTopicParams params = new UpdateTopicParams(topic.getId(), updateTopicDto.getName());
            final Topic  updated = this.topicRepository.updateTopic(params);
            return Response.ok(updated).build();
        } catch (DuplicateException e) {
            return Response.status(400).entity("Thema mit gleichem Namen existiert bereits.").build();
        }

    }

    @Override
    protected Optional<Topic> getEntity(String entityId) {
        return this.topicRepository.getTopic(Integer.parseInt(entityId));
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

        if(!project.canEdit()){
            return Response.status(400).entity("Projekt kann im aktuellen Status nicht bearbeitet werden.").build();
        }

        this.topicRepository.deleteTopic(topicId);
        return Response.ok().build();
    }
}
