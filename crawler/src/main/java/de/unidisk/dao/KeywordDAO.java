package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.Keyword;
import de.unidisk.entities.Topic;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;


public class KeywordDAO {

    public KeywordDAO() {
    }

    public List<Keyword> addKeywords(List<Pair<String, String>> keywordWithTopic) {
        List<Keyword> keywords = new ArrayList<>();
        Session currentSession = HibernateUtil.getSesstionFactory().openSession();
        Transaction tnx = currentSession.beginTransaction();
        for (Pair<String, String> keyTop : keywordWithTopic) {
            Keyword keyword = currentSession.createQuery("select k from Keyword k inner join k.topics where k.name like :name ", Keyword.class)
                    .setParameter("name", keyTop.getKey())
                    .uniqueResultOptional()
                    .orElse(new Keyword(keyTop.getKey()));
            Topic topicPojo = new Topic();
            topicPojo.setName(keyTop.getValue());
            keyword.getTopics().add(topicPojo);
            try {
                currentSession.save(topicPojo);
            } catch (NonUniqueObjectException exp) {
                System.out.println("Object already safed");
            }
            currentSession.saveOrUpdate(keyword);
            keywords.add(keyword);
        }
        tnx.commit();
        currentSession.close();
        return keywords;
    }

    /**
     * Find keywords by topic name
     */
    public List<Keyword> findKeyWords(String topic) {
        Session currentSession = HibernateUtil.getSesstionFactory().openSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        List<Keyword> keywords = currentSession.createQuery("select k from Keyword k inner join k.topics t where t.name like :name", Keyword.class)
                .setParameter("name", topic)
                .list();
        transaction.commit();
        currentSession.close();
        return keywords;
    }
}
