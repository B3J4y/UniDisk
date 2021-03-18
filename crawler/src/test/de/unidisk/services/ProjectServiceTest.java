package de.unidisk.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.HibernateLifecycle;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.HibernateKeywordRepo;
import de.unidisk.repositories.HibernateProjectRepo;
import de.unidisk.repositories.HibernateTopicRepo;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectServiceTest implements HibernateLifecycle {


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
            }
        }
        return project;
    }

    @Test
    public void copyProjectWithoutSuggestionsSucceeds() throws DuplicateException, EntityNotFoundException {
        final IProjectRepository projectRepository = new HibernateProjectRepo();
        final ITopicRepository topicRepository = new HibernateTopicRepo();
        final IKeywordRepository keywordRepository = new HibernateKeywordRepo();

        final ProjectService projectService = new ProjectService(
                projectRepository,
                topicRepository,
                keywordRepository
        );

        final Map<String,List<String>> topicKeywordsMap = Map.ofEntries(
                entry("Custom", Arrays.asList("Katze","Hund","Löwe","Tiger")),
                entry("Mixed", Arrays.asList("Schokolade","Test","Schokolade", "Ente")),
                // all words are suggestions
                entry("Stichwortlos", Arrays.asList("Baum","Haus"))
        );

        final Set<String> suggestedKeywordsSet = Set.of("Test","Schokolade", "Haus", "Baum");

        final Project project = createProject("test",topicKeywordsMap
        , suggestedKeywordsSet);

        final Project copiedProject = projectService.copyProjectWithoutSuggestions(String.valueOf(project.getId()));
        topicKeywordsMap.entrySet().forEach(entry -> {
            final String entryTopic = entry.getKey();
            final Optional<Topic> copiedTopic = copiedProject.getTopics().stream().filter(topic -> topic.getName().equals(entryTopic)).findFirst();
            assertTrue(copiedTopic.isPresent());
            final int expectedKeywordCount = entry.getValue().stream().map(keyword -> suggestedKeywordsSet.contains(keyword) ? 0 : 1 ).reduce(0, Integer::sum);
            assertEquals(expectedKeywordCount,copiedTopic.get().getKeywords().size() );
        });
    }
}
