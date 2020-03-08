package de.unidisk.dao;

import de.unidisk.entities.hibernate.*;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.view.model.KeywordItem;
import de.unidisk.view.model.MapMarker;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectDAO  implements IProjectRepository {
    public ProjectDAO() {
    }

    public Project createProject(String name) {
        Optional<Project> existingProject = findProject(name);
        if (existingProject.isPresent()) {
            return existingProject.get();
        }
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        Project project = new Project(name);
        project.setProjectState(ProjectState.IDLE);
        currentSession.save(project);

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

    @Override
    public List<ProjectView> getProjects() {
        return getAll().stream().map(ProjectView::fromProject).collect(Collectors.toList());
    }

    @Override
    public Project getProject(String projectId) {
        int id = Integer.parseInt(projectId);
        final Optional<Project> p  = findProjectById(id);
        if(p.isPresent())
            return p.get();
        return null;
    }

    @Override
    public List<KeywordItem> getProjectKeywords(String projectId) {
        return null;
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
        return state.isPresent() ? state.get() == ProjectState.IDLE : false;
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

    private Result mapKeywordScoreToResult(KeyWordScore r){
        return new Result(r.getUniName(),r.getScore(),r.getId());
    }


    public List<Result> getResults(String projectId)
    {
        int pId = Integer.parseInt(projectId);
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
       List<KeyWordScore> scores = currentSession.createQuery("select kScore from KeyWordScore kScore where kScore.keyword.topicId in " +
                "(select t.id from Topic t where t.projectId = :pId)", KeyWordScore.class)
                .setParameter("pId",pId).list();
        transaction.commit();
        currentSession.close();
        return scores.stream().map(this::mapKeywordScoreToResult).collect(Collectors.toList());
    }

    @Override
    public List<MapMarker> getMarker(String projectId) {
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

    @Override
    public List<Project> getProjects(ProjectState state){
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }

        List<Project> project = currentSession.createQuery("select p from Project p where p.projectState = :state", Project.class).setParameter("state",state).list();
        transaction.commit();
        currentSession.close();
        return project;
    }
}
