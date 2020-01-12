package de.unidisk.entities.hibernate;

import de.unidisk.common.ApplicationState;
import de.unidisk.dao.*;
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
            final Project dbProject = projectDAO.createProject(p.getName());
            if(dbProject == null){
                //project already exists, might happen if setup is run multiple times against persistent database
                return;
            }
            p.setId(dbProject.getId());
            projectDAO.updateProjectState(dbProject.getId(),p.getProjectState());
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
