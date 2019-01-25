package de.unidisk.entities;

import de.unidisk.HibernateUtil;
import de.unidisk.dao.KeywordDAO;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface HibernateLifecycle {
    @AfterEach
    default void cleanUpDatabase() {
        HibernateUtil.truncateTable(KeyWordScore.class);
        HibernateUtil.truncateTable(TopicScore.class);
        HibernateUtil.truncateTable(Keyword.class);
        HibernateUtil.truncateTable(Topic.class);
        HibernateUtil.truncateTable(SearchMetaData.class);
        HibernateUtil.truncateTable(University.class);
    }

    default List<Keyword> createHalloWeltTopic() {
        List<Pair<String, String>> keyTop = new ArrayList<>();
        keyTop.add(Pair.of("Hallo", "Hallo Welt"));
        keyTop.add(Pair.of("Welt", "Hallo Welt"));

        KeywordDAO kDao = new KeywordDAO();
        return kDao.addKeywords(keyTop);
    }
}
