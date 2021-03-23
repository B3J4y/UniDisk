package de.unidisk.dao;

import de.unidisk.common.ApplicationState;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.dao.*;
import de.unidisk.entities.hibernate.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
                try {
                    SearchMetaData metaData = searchMetaDataDAO.createMetaData(new URL("http://www.uni-potsdam.de/home"), state.getUniversities().get(0).getId(),
                            ZonedDateTime.now().toEpochSecond());

                    topicScoreDAO.addScore(dbTopic,1,metaData);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                dbTopic.getKeywords().forEach(keyword -> {
                    assert(keyword.getId() != 0);

                    final int scores = 1;
                    for(int i= 0; i < scores;i++){
                        final double score = new Random().nextDouble();

                        final University uni = state.getUniversities().get(new Random().nextInt(universityNames.size()));
                        SearchMetaDataDAO smdDAO = new SearchMetaDataDAO();
                        SearchMetaData metaData = null;
                        try {
                            metaData = smdDAO.createMetaData(new URL(randomUniversityUrl(uni.getName())), uni.getId(),
                                    ZonedDateTime.now().toEpochSecond());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        KeywordScoreDAO scoreDAO = new KeywordScoreDAO();
                        scoreDAO.addScore(keyword, score, metaData);
                    }
                });
            });
        });


    }
}
