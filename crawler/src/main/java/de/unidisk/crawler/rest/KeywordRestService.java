package de.unidisk.crawler.rest;

import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.crawler.rest.authentication.ContextUser;
import de.unidisk.crawler.rest.dto.keyword.CreateKeywordDto;
import de.unidisk.crawler.rest.dto.topic.CreateTopicDto;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.Optional;

@Path("/keyword")
public class KeywordRestService extends CRUDService<Keyword, CreateKeywordDto> {

    @Inject
    ITopicRepository topicRepository;

    @Inject
    IProjectRepository projectRepository;

    @Inject
    IKeywordRepository keywordRepository;


    @Override
    protected Response executeCreate(ContextUser user,CreateKeywordDto dto) {
        final Optional<Topic> topic = this.topicRepository.getTopic(Integer.parseInt(dto.getTopicId()));
        if(!topic.isPresent()){
            return Response.status(400).entity("Projekt mit ID existiert nicht.").build();
        }
        final Optional<Project> project = this.projectRepository.getProject(String.valueOf(topic.get().getProjectId()));
        if(!project.get().getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        final IKeywordRepository.CreateKeywordArgs args = new IKeywordRepository.CreateKeywordArgs(dto.getName(),dto.getTopicId());
        final Keyword keyword = this.keywordRepository.createKeyword(args);
        return Response.ok(keyword).build();
    }

    @Override
    protected Response executeDelete(ContextUser user, String id) {
        final Optional<Keyword> optionalKeyword = this.keywordRepository.getKeyword(Integer.parseInt(id));
        if(!optionalKeyword.isPresent()){
            return Response.status(404).build();
        }
        final Keyword keyword = optionalKeyword.get();
        final Topic topic = this.topicRepository.getTopic(keyword.getTopicId()).get();
        final Project project = this.projectRepository.getProject(String.valueOf(topic.getProjectId())).get();
        if(!project.getUserId().equals(user.getId())){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        final boolean deleted = this.keywordRepository.deleteKeyword(keyword.getId());
        return Response.ok(deleted).build();
    }
}
