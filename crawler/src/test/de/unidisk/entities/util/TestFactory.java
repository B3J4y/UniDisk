package de.unidisk.entities.util;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.dao.TopicDAO;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.SearchMetaData;
import de.unidisk.entities.hibernate.Topic;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.Key;
import java.util.*;

public final class TestFactory {

    private TestFactory(){}

    public static  List<Topic> createTwoTopics() {
        List<Topic> keyTop = new ArrayList<>();
        keyTop.add(new Topic("test",6));
        keyTop.add(new Topic("test2",6));

        return keyTop;
    }

    public static Project createRawProject(){
        try {
            return new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs(UUID.randomUUID().toString(),"test"));
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Keyword createKeyword(){
        Project p = null;
        try {
            p = new ProjectDAO().createProject(new IProjectRepository.CreateProjectArgs(UUID.randomUUID().toString(),"test"));
        } catch (DuplicateException e) {
            e.printStackTrace();
        }
        Topic t = new TopicDAO().createTopic(UUID.randomUUID().toString(),p.getId());
        Keyword k = new KeywordDAO().addKeyword(UUID.randomUUID().toString(),t.getId());
        return k;
    }

    public static String randomUniversityUrl(String university){
        String generatedString = RandomStringUtils.random(10, true, true);
        return String.format("https://%s.de/%s.html",university,generatedString);
    }

    public static String randomUniversityUrl(List<String> universityNames){
        final String university = universityNames.get(new Random().nextInt(universityNames.size()));
        return randomUniversityUrl(university);
    }

    public static SearchMetaData randomSearchData(String url){
        return new SearchMetaData(
                url,
                new Date().getTime()
        );
    }
}
