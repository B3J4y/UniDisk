package de.unidisk.dao;

import de.unidisk.common.ApplicationState;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.entities.hibernate.University;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class HibernateTestSetup {

    static String randomUniversityUrl(String university){
        String generatedString = RandomStringUtils.random(10, true, true);
        return String.format("https://%s.de/%s.html",university,generatedString);
    }

    public static void Setup(ApplicationState state){
        // current user id used for all users
        final String userId = "0";
        final ProjectDAO projectDAO = new ProjectDAO();
        final TopicDAO topicDAO = new TopicDAO();
        final TopicScoreDAO topicScoreDAO = new TopicScoreDAO();

        final SearchMetaDataDAO searchMetaDataDAO = new SearchMetaDataDAO();

        final UniversityDAO uniDao = new UniversityDAO();
        state.getUniversities().forEach((u) -> {
            final University dbUni = uniDao.addUniversity(u);
            u.setId(dbUni.getId());
        });
        final List<String> universityNames = state.getUniversities().stream().map(University::getName).collect(Collectors.toList());

        state.getProjectList().forEach((p) -> {
            final Project dbProject;
            Project dbProject1;

            try {
                dbProject1 = projectDAO.createProject(new CreateProjectParams(userId,p.getName()));
            } catch (DuplicateException e) {
                e.printStackTrace();
                dbProject1 = null;
            }

            dbProject = dbProject1;
            if(dbProject == null){
                //project already exists, might happen if setup is run multiple times against persistent database
                return;
            }
            p.setId(dbProject.getId());


            projectDAO.updateProjectState(dbProject.getId(),p.getProjectState());
            if(p.getProcessingError() != null)
                projectDAO.setProjectError(p.getId(),p.getProcessingError());
            p.getTopics().forEach((topic) -> {
                final Topic dbTopic = topicDAO.createTopic(topic.getName(),dbProject.getId()
                        ,topic.getKeywords().stream().map(Keyword::getName)
                                .collect(Collectors.toList()));
            });
        });


    }
}
