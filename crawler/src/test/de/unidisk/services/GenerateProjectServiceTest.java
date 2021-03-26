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
import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class GenerateProjectServiceTest implements HibernateLifecycle {

    @Mock
    IKeywordRecommendationService keywordRecommendationService;

    @BeforeAll
    public void openMocks() {
        MockitoAnnotations.initMocks(this);
    }


    private Project createProject(String name, Map<String,List<String>> topicKeywords , Set<String> suggestedKeys) throws DuplicateException {
        final Project project = new ProjectDAO().createProject(new CreateProjectParams("test",name));
        final TopicDAO topicDAO = new TopicDAO();
        final KeywordDAO keywordDAO = new KeywordDAO();
        for (Map.Entry<String, List<String>> entry : topicKeywords.entrySet()) {
            final String topicName = entry.getKey();
            final List<String> keywords = entry.getValue();
            final Topic topic = topicDAO.createTopic(topicName,project.getId());
            for(String keyword : keywords){
                final boolean isSuggestion = suggestedKeys.contains(keyword);
                final Keyword createdKeyword = keywordDAO.createKeyword(new CreateKeywordParams(
                        keyword,
                        String.valueOf(topic.getId()),
                        isSuggestion
                ));
                topic.getKeywords().add(createdKeyword);
            }
            project.getTopics().add(topic);
        }
        return project;
    }

    @Test
    public void copyProjectFromTopics() throws DuplicateException, EntityNotFoundException {
        final IProjectRepository projectRepository = new HibernateProjectRepo();
        final ITopicRepository topicRepository = new HibernateTopicRepo();
        final IKeywordRepository keywordRepository = new HibernateKeywordRepo();

        final ProjectGenerationService projectService = new ProjectGenerationService(
                projectRepository,
                topicRepository,
                keywordRepository,
                keywordRecommendationService
        );


        final Map<String,List<String>> topicKeywordsMap = new HashMap<>();
        topicKeywordsMap.put("Custom", Arrays.asList("Katze","Hund","Löwe","Tiger"));
        topicKeywordsMap.put("Mixed", Arrays.asList("Schokolade","Test", "Ente"));
        topicKeywordsMap.put("Stichwortlos", Arrays.asList("Baum","Haus"));

        final List<String> customResults = Arrays.asList("1","2","Hund", "Katze","Bär", "Baum");

        when(keywordRecommendationService.getTopicRecommendations(any())).thenAnswer(invocationOnMock -> {
            return new RecommendationResult(
                    customResults.stream().map(r -> new KeywordRecommendation(.5,r)).collect(Collectors.toList()),
                    "24"
            );
        });


        final Project project = createProject("test",topicKeywordsMap
                ,new HashSet());

        final Project newProject = projectService.generateProjectFromTopics(String.valueOf(project.getId()));
        assertEquals(project.getTopics().size(), newProject.getTopics().size());
        newProject.getTopics().forEach(topic-> {
            assertEquals(topic.getKeywords().size(), 5);
        });
    }
}