package de.unidisk.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.services.IProjectService;
import de.unidisk.contracts.services.keyword.IKeywordRecommendationService;
import de.unidisk.contracts.services.keyword.KeywordRecommendation;
import de.unidisk.contracts.services.keyword.KeywordRecommendationResponse;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.entities.hibernate.ProjectSubtype;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.view.project.ProjectView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SessionScoped
@ManagedBean(name = "projectService")
public class ProjectService implements IProjectService {

    @ManagedProperty("#{projectRepository}")
    IProjectRepository projectRepository;
    ITopicRepository topicRepository;
    IKeywordRepository keywordRepository;
    IKeywordRecommendationService keywordService;

    public ProjectService() {
    }

    public ProjectService(IProjectRepository projectRepository, ITopicRepository topicRepository, IKeywordRepository keywordRepository, IKeywordRecommendationService keywordService) {
        this.projectRepository = projectRepository;
        this.topicRepository = topicRepository;
        this.keywordRepository = keywordRepository;
        this.keywordService = keywordService;
    }

    public List<ProjectView> getProjects() {
        return projectRepository.getProjects();
    }

    @Override
    public Project getProject(String projectId) {
        return projectRepository.getProject(projectId).get();
    }


    public boolean deleteProject(String projectId){
        return projectRepository.deleteProject(projectId);
    }

    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project copyProjectWithoutSuggestions(String parentProjectId) throws EntityNotFoundException, DuplicateException {
        final Optional<Project> optionalParentProject = projectRepository.getProject(parentProjectId);
        if(!optionalParentProject.isPresent())
                throw new EntityNotFoundException(Project.class,Integer.parseInt(parentProjectId));

        final CreateProjectParams params = CreateProjectParams.subproject(Integer.parseInt(parentProjectId), ProjectSubtype.CustomOnly);

        final Project subproject = projectRepository.createProject(params);
        final Project parentProject = optionalParentProject.get();
        for (Topic topic : parentProject.getTopics()) {
            final Topic topicCopy = topicRepository.createTopic(subproject.getId(), topic.getName());

            final List<Keyword> customKeywords = topic.getKeywords().stream().filter(keyword -> !keyword.isSuggestion()).collect(Collectors.toList());
            for (Keyword keyword : customKeywords) {
                final CreateKeywordParams keywordParams = new CreateKeywordParams(keyword.getName(), String.valueOf(topicCopy.getId()), false);
                final Keyword copiedKeyword = keywordRepository.createKeyword(keywordParams);
                topicCopy.getKeywords().add(copiedKeyword);
            }
            subproject.getTopics().add(topicCopy);
        }

        return subproject;
    }

    public Project copyProjectFromTopics(String parentProjectId) throws EntityNotFoundException, DuplicateException {
        final Optional<Project> optionalParentProject = projectRepository.getProject(parentProjectId);
        if(!optionalParentProject.isPresent())
            throw new EntityNotFoundException(Project.class,Integer.parseInt(parentProjectId));

        final CreateProjectParams params = CreateProjectParams.subproject(Integer.parseInt(parentProjectId), ProjectSubtype.ByTopics);

        final Project subproject = projectRepository.createProject(params);
        final Project parentProject = optionalParentProject.get();
        for (Topic topic : parentProject.getTopics()) {
            final Topic topicCopy = topicRepository.createTopic(subproject.getId(), topic.getName());
            final KeywordRecommendationResponse response = keywordService.getRecommendations(topic.getName(), Collections.emptyList());
            final List<String> keywords = response.getRecommendations().stream().limit(5).map(KeywordRecommendation::getKeyword).collect(Collectors.toList());
            for(String keyword : keywords){
                final CreateKeywordParams keywordParams = new CreateKeywordParams(keyword, String.valueOf(topicCopy.getId()), false);
                final Keyword createdKeyword = keywordRepository.createKeyword(keywordParams);
                topicCopy.getKeywords().add(createdKeyword);
            }
            subproject.getTopics().add(topicCopy);
        }

        return subproject;
    }


}
