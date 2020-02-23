package de.unidisk.entities;

import de.unidisk.dao.HibernateUtil;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.entities.hibernate.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface HibernateLifecycle {

    @BeforeEach
    default void cleanUpDatabase() {
        final HibernateUtil.DatabaseConfig config = HibernateUtil.DatabaseConfig.Memory;

        HibernateUtil.setSessionFactory(config);
        if(config == HibernateUtil.DatabaseConfig.Memory)
            return;

        HibernateUtil.truncateTable(KeyWordScore.class);
        HibernateUtil.truncateTable(TopicScore.class);
        HibernateUtil.truncateTable(Keyword.class);
        HibernateUtil.truncateTable(Project.class);
        HibernateUtil.truncateTable(Topic.class);
        HibernateUtil.truncateTable(SearchMetaData.class);
        HibernateUtil.truncateTable(University.class);
    }
}
