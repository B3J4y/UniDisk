package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.Project;
import de.unidisk.entities.Topic;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class ProjectDAO {
    public ProjectDAO() {
    }

    public boolean createProject(String name) {
        boolean isProjectPresent = findProject(name).isPresent();
        if (isProjectPresent) {
            return false;
        }
        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Project project = new Project(name);
        currentSession.save(project);
        transaction.commit();
        currentSession.close();
        return true;
    }

    public Optional<Project> findProject(String name) {
        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        Optional<Project> optProj = currentSession.createQuery("select p from Project p where p.name like :name", Project.class)
                .setParameter("name", name)
                .uniqueResultOptional();

        transaction.commit();
        currentSession.close();
        return optProj;
    }

    public void addTopicToProject(String name, String topicName) {
        Project project = findProject(name).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        TopicDAO topicDAO = new TopicDAO();
        Topic topic = topicDAO.findOrCreateTopic(topicName)
                .orElseThrow(() -> new IllegalArgumentException("Could not create Topic"));
        project.addTopic(topic);

        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        currentSession.update(project);
        transaction.commit();
        currentSession.close();
    }

    public List<Project> getAll() {
        Session currentSession = HibernateUtil.getSesstionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        List<Project> project = currentSession.createQuery("select p from Project p", Project.class).list();
        transaction.commit();
        currentSession.close();
        return project;
    }
}
