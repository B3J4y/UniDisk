package de.unidisk.crawler.rest;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.crawler.rest.authentication.ContextUser;
import de.unidisk.crawler.rest.dto.keyword.CreateKeywordDto;
import de.unidisk.crawler.rest.dto.keyword.UpdateKeywordDto;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/keyword")
public class KeywordRestService extends CRUDService<Keyword, CreateKeywordDto, UpdateKeywordDto> {

    @Inject
    ITopicRepository topicRepository;

    @Inject
    IProjectRepository projectRepository;

    @Inject
    IKeywordRepository keywordRepository;
    public KeywordRestService(){

    }

    public KeywordRestService(ITopicRepository topicRepository, IProjectRepository projectRepository, IKeywordRepository keywordRepository) {
        this.topicRepository = topicRepository;
        this.projectRepository = projectRepository;
        this.keywordRepository = keywordRepository;
    }

    @Override
    protected Response executeCreate(ContextUser user,CreateKeywordDto dto) {
        final Optional<Topic> topic = this.topicRepository.getTopic(Integer.parseInt(dto.getTopicId()));
        if(!topic.isPresent()){
            return Response.status(400).entity("Thema mit ID existiert nicht.").build();
        }
        // Calling get should be safe as the topic exists and a topic can't exist without project.
        final Project project = this.projectRepository.getProject(String.valueOf(topic.get().getProjectId())).get();
        if(!project.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if(!project.canEdit()){
            return Response.status(400).entity("Projekt kann im aktuellen Status nicht bearbeitet werden.").build();
        }


        final IKeywordRepository.CreateKeywordArgs args = new IKeywordRepository.CreateKeywordArgs(dto.getName(),dto.getTopicId());
        final Keyword keyword;
        try {
            keyword = this.keywordRepository.createKeyword(args);
        } catch (DuplicateException e) {
            return Response.status(400).entity("Stichwort mit Namen existiert bereits.").build();
        }
        return Response.ok(keyword).build();
    }

    @Override
    protected Response executeUpdate(ContextUser user, UpdateKeywordDto updateKeywordDto, Keyword keyword) {
        final Topic topic = this.topicRepository.getTopic(keyword.getTopicId()).get();
        final Project project = this.projectRepository.getProject(String.valueOf(topic.getProjectId())).get();
        if(!project.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try {
            final IKeywordRepository.UpdateKeywordArgs args = new IKeywordRepository.UpdateKeywordArgs(updateKeywordDto.getName(), updateKeywordDto.getKeywordId());
            final Keyword updated = this.keywordRepository.updateKeyword(args);
            return Response.ok(updated).build();
        } catch (DuplicateException e) {
            return Response.status(400).entity("Stichwort mit Namen existiert bereits.").build();
        }
    }

    @Override
    protected Optional<Keyword> getEntity(String entityId) {
        return this.keywordRepository.getKeyword(Integer.parseInt(entityId));
    }

    @Override
    protected Response executeDelete(ContextUser user, String id) {
        final Optional<Keyword> optionalKeyword = this.keywordRepository.getKeyword(Integer.parseInt(id));
        if(!optionalKeyword.isPresent()){
            return Response.status(404).build();
        }
        final Keyword keyword = optionalKeyword.get();
        final Topic topic = this.topicRepository.getTopic(keyword.getTopicId()).get();
        final String projectId = String.valueOf(topic.getProjectId());
        final Project project = this.projectRepository.getProject(projectId).get();
        if(!project.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if(!project.canEdit()){
            return Response.status(400).entity("Projekt kann im aktuellen Status nicht bearbeitet werden.").build();
        }

        final boolean deleted = this.keywordRepository.deleteKeyword(keyword.getId());
        return Response.ok(deleted).build();
    }
}
