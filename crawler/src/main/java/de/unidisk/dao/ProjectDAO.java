package de.unidisk.dao;

import de.unidisk.HibernateUtil;
import de.unidisk.entities.hibernate.*;
import de.unidisk.view.model.MapMarker;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class ProjectDAO  {
    public ProjectDAO() {
    }

    public Project createProject(String name) {
        boolean isProjectPresent = findProject(name).isPresent();
        if (isProjectPresent) {
            return null;
        }
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Project project = new Project(name);
        project.setProjectState(ProjectState.WAITING);
        currentSession.save(project);
        System.out.println("project id " + project.getId());
        transaction.commit();
        currentSession.close();
        return project;
    }

    public void updateProjectState(int projectId, ProjectState state) {
        final Optional<Project> p = findProjectById(projectId);
        if (!p.isPresent()) {
            return;
        }
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        final Project project = p.get();
        project.setProjectState(state);

        currentSession.update(project);
        transaction.commit();
        currentSession.close();
    }

    public boolean deleteProject(String name){

        Optional<Project> project = findProject(name);
        if(!project.isPresent())
            return false;

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        currentSession.delete(project.get());

        transaction.commit();
        currentSession.close();
        return true;
    }

    public boolean deleteProjectById(String id){

        Optional<Project> project = findProjectById(Integer.parseInt(id));
        if(!project.isPresent())
            return false;

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        currentSession.delete(project.get());

        transaction.commit();
        currentSession.close();
        return true;
    }

    public boolean canEdit(String projectId){
        int pId = Integer.parseInt(projectId);
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Query stateQuery = currentSession.createQuery("select p.projectState from Project p WHERE p.id = :pId")
                .setParameter("pId",pId)
                ;
        Optional<ProjectState> state = (Optional<ProjectState>) stateQuery.uniqueResultOptional();
        transaction.commit();
        currentSession.close();
        return state.isPresent() ? state.get() == ProjectState.WAITING : false;
    }

    public Optional<Project> findProjectById(int id) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        Optional<Project> optProj = currentSession.createQuery("select p from Project p where p.id = :id", Project.class)
                .setParameter("id", id)
                .uniqueResultOptional();

        transaction.commit();
        currentSession.close();
        return optProj;
    }

    public Optional<Project> findProject(String name) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
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

        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        currentSession.update(project);
        transaction.commit();
        currentSession.close();
    }

    public List<Project> getAll() {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        List<Project> project = currentSession.createQuery("select p from Project p", Project.class).list();
        transaction.commit();
        currentSession.close();
        return project;
    }

    public List<KeyWordScore> getResults(String projectId)
    {
        int pId = Integer.parseInt(projectId);
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        //currentSession.createQuery("select t from TopicScore t where t.topic.id in (select t.id from topic t where t.projectId = :pId)", TopicScore.class).setParameter("pId",pId);
        List<KeyWordScore> scores = currentSession.createQuery("select kScore from KeyWordScore kScore where kScore.keyword.topicId in " +
                "(select t.id from Topic t where t.projectId = :pId)", KeyWordScore.class)
                .setParameter("pId",pId).list();
        /*List<KeyWordScore> scores = currentSession.createQuery("select score from Topic t " +
                "INNER JOIN Keyword k ON t.id = k.topicId " +
                "LEFT JOIN KeyWordScore  score ON score.keyword.id = k.id " +
                "WHERE t.projectId = :pId", KeyWordScore.class)
                .setParameter("pId",pId)
                .list();*/
        transaction.commit();
        currentSession.close();
        return scores;
    }

    public List<MapMarker> getMapMarker(String projectId){
        int pId = Integer.parseInt(projectId);
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        List<MapMarker> scores = currentSession.createQuery("select new de.unidisk.view.model.MapMarker(tScore.topic.name,tScore.topic.id, tScore.searchMetaData.university) from TopicScore tScore where tScore.topic.projectId = :pId")
                .setParameter("pId",pId).list();

        transaction.commit();
        currentSession.close();
        return scores;
    }
}
