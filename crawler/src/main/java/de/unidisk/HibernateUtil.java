package de.unidisk;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSesstionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            sessionFactory = config.configure().buildSessionFactory();
        }
        return sessionFactory;
    }

    public static <T> void truncateTable(Class<T> entityClass) {
        Session session = HibernateUtil.getSesstionFactory().openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<T> query = cb.createCriteriaDelete(entityClass);
        query.from(entityClass);
        session.getTransaction().begin();
        session.createQuery(query).executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
