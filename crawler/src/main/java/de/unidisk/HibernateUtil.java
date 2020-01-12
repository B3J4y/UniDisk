package de.unidisk;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



public class HibernateUtil {
    private static SessionFactory sessionFactory;


    private static final String sqlConfig = "hibernate.cfg.xml";
    private static final String memoryConfig = "hibernate.cfg.mem.xml";
    private static String config = sqlConfig;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            sessionFactory = config.configure(HibernateUtil.config).buildSessionFactory();
        }
        return sessionFactory;
    }

    public static <T> void truncateTable(Class<T> entityClass) {
        if(HibernateUtil.config == HibernateUtil.memoryConfig)
            return;

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.getTransaction().begin();
        session.createSQLQuery("truncate table " + entityClass.getSimpleName()).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
