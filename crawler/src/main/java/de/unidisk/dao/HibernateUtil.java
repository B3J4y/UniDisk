package de.unidisk.dao;

import de.unidisk.config.SystemConfiguration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public enum DatabaseConfig {Memory, MySql}

    private static final String sqlConfig = "hibernate.cfg.xml";
    private static final String memoryConfig = "hibernate.cfg.mem.xml";
    private static String config = memoryConfig;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            sessionFactory = config.configure(SystemConfiguration.getInstance().getDatabaseConfiguration().getConfigFile()).buildSessionFactory();
        }
        return sessionFactory;
    }

    public static void setSessionFactory(DatabaseConfig dbConfig) {
        Configuration config = new Configuration();
        String configFile;
        switch(dbConfig){
            case MySql:
                configFile = HibernateUtil.sqlConfig;
                break;
            default:
                configFile = HibernateUtil.memoryConfig;
        }

        sessionFactory = config.configure(configFile).buildSessionFactory();
        HibernateUtil.config = configFile;
    }

    public static <T> void truncateTable(Class<T> entityClass) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.createSQLQuery("truncate table " + entityClass.getSimpleName()).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
