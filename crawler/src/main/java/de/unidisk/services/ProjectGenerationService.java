package de.unidisk.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.KeywordRecommendation;
import de.unidisk.contracts.services.recommendation.RecommendationResult;
import de.unidisk.entities.hibernate.*;

import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ProjectGenerationService {
    IProjectRepository projectRepository;
    ITopicRepository topicRepository;
    IKeywordRepository keywordRepository;
    IKeywordRecommendationService keywordRecommendationService;


    public ProjectGenerationService(IProjectRepository projectRepository, ITopicRepository topicRepository, IKeywordRepository keywordRepository, IKeywordRecommendationService keywordRecommendationService) {
        this.projectRepository = projectRepository;
        this.topicRepository = topicRepository;
        this.keywordRepository = keywordRepository;
        this.keywordRecommendationService = keywordRecommendationService;
    }

    public List<Project> generateSubprojects(String projectId) throws DuplicateException, EntityNotFoundException {
        final List<Project> subprojects = new ArrayList<>();
        subprojects.add(generateProjectFromTopics(projectId));
        subprojects.add(projectRepository.generateSubprojectByCustom(projectId));
        subprojects.forEach(project -> {
            // enqueue for processing
            projectRepository.updateProjectState(project.getId(), ProjectState.WAITING);
        });
        return subprojects;
    }

    public Project generateProjectFromTopics(String projectId) throws DuplicateException, EntityNotFoundException {
        final Project project = projectRepository.getProjectDetailsOrFail(projectId);
        final Project projectCopy = projectRepository.createProject(CreateProjectParams.subproject(
                Integer.parseInt(projectId),
                ProjectSubtype.BY_TOPICS
        ));
        final List<Topic> topics = new ArrayList<>();
        final int projectCopyId = projectCopy.getId();
        for(Topic topic: project.getTopics()){
            final String topicName = topic.getName();
            final Topic topicCopy = topicRepository.createTopic(projectCopyId, topicName);
            final RecommendationResult result = keywordRecommendationService.getTopicRecommendations(topic.getName());
            final List<KeywordRecommendation> recommendations = result.getRecommendations().stream().limit(5).collect(Collectors.toList());
            final List<Keyword> keywords = new ArrayList<>();
            final String topicId = String.valueOf(topicCopy.getId());
            for(KeywordRecommendation recommendation : recommendations){
                final Keyword keyword = keywordRepository.createKeyword(
                        new CreateKeywordParams(
                                recommendation.getKeyword(),
                                topicId,
                                true
                        )
                );
                keywords.add(keyword);
            }
            topicCopy.setKeywords(keywords);
            topics.add(topicCopy);
        }
        projectCopy.setTopics(topics);


        return projectCopy;
    }

}
