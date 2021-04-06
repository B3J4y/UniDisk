package de.unidisk.dao;

import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.exceptions.DuplicateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import java.util.function.Function;


public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public enum DatabaseConfig {Memory, MySql}

    private static final String sqlConfig = "hibernate.cfg.xml";
    private static final String memoryConfig = "hibernate.cfg.mem.xml";
    private static String config = memoryConfig;

    private static final String CONNECTION_URL_PROP = "hibernate.connection.url";

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            final String configurationFile = SystemConfiguration.getInstance().getDatabaseConfiguration().getConfigFile();
            config = config.configure(configurationFile);
            final String dockerEnvValue =  System.getenv("DOCKER_COMPOSE");
            final boolean isDockerEnv = dockerEnvValue != null && dockerEnvValue.equals("1");
            if(isDockerEnv){
                final String connectionUrl = config.getProperty(CONNECTION_URL_PROP);
                final boolean isMemoryDb = connectionUrl.startsWith("jdbc:h2:mem");
                if(!isMemoryDb){
                    // changes jdbc:mysql://localhost:3306/unidisk to jdbc:mysql://db:3306/unidisk
                    final String containerConnectionUrl = connectionUrl.replace("localhost:","db:");
                    config = config.setProperty(CONNECTION_URL_PROP, containerConnectionUrl);
                }
            }
            sessionFactory = config.buildSessionFactory();
        }
        return sessionFactory;
    }

    public static <T> T execute(Function<Session,T> action){
        Session sess = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = sess.beginTransaction();
            final T result = action.apply(sess);
            tx.commit();
            return result;
        }
        catch (Exception e) {
            if (tx!=null) tx.rollback();

            throw e;
        }
        finally {
            sess.close();
        }
    }

    public static <T> T executeUpdate(Function<Session,T> action) throws DuplicateException {
        Session sess = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = sess.beginTransaction();
            final T result = action.apply(sess);
            tx.commit();
            return result;
        }
        catch (Exception e) {
            if (tx!=null) tx.rollback();

            if(e instanceof javax.persistence.PersistenceException && e.getCause() instanceof  ConstraintViolationException)
                throw new DuplicateException();
            throw e;
        }
        finally {
            sess.close();
        }
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
