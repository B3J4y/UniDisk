package de.unidisk.services;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.services.IProjectService;
import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.KeywordRecommendation;
import de.unidisk.contracts.services.recommendation.RecommendationResult;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.entities.hibernate.ProjectSubtype;
import de.unidisk.entities.hibernate.Topic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Project generateProjectFromTopics(String projectId) throws DuplicateException {

        final Optional<Project> optionalProject = projectRepository.getProjectDetails(projectId);
        if(!optionalProject.isPresent())
                return null;
        final Project project = optionalProject.get();
        final Project projectCopy = projectRepository.createProject(CreateProjectParams.subproject(
                Integer.parseInt(projectId),
                ProjectSubtype.BY_TOPICS
        ));
        final List<Topic> topics = new ArrayList<>();
        for(Topic topic: project.getTopics()){
            final Topic topicCopy = topicRepository.createTopic(projectCopy.getId(), topic.getName());
            final RecommendationResult result = keywordRecommendationService.getTopicRecommendations(topic.getName());
            final List<KeywordRecommendation> recommendations = result.getRecommendations().stream().limit(5).collect(Collectors.toList());
            final List<Keyword> keywords = new ArrayList<>();
            for(KeywordRecommendation recommendation : recommendations){
                final Keyword keyword = keywordRepository.createKeyword(
                        new CreateKeywordParams(recommendation.getKeyword(), String.valueOf(topicCopy.getId()), true)
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
