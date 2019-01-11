package de.unidisk.entities;

import de.unidisk.HibernateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface HibernateLifecycle {
    @AfterEach
    default void cleanUpDatabase() {
        HibernateUtil.truncateTable(KeyWordScore.class);
        HibernateUtil.truncateTable(SearchMetaData.class);
        HibernateUtil.truncateTable(University.class);
        HibernateUtil.truncateTable(Keyword.class);
        HibernateUtil.truncateTable(Topic.class);
    }
}
