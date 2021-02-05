package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.entities.hibernate.*;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.view.model.KeywordItem;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectDAO  implements IProjectRepository {

    public ProjectDAO() {
    }

    public Project createProject(CreateProjectArgs args) {
        Project project = new Project(args.getName());
        project.setUserId(args.getUserId());
        project.setProjectState(ProjectState.IDLE);
        return  HibernateUtil.execute(session -> {
            session.save(project);
            project.setTopics(new ArrayList<>());
            return project;
        });
    }

    @Override
    public Project updateProject(String id, String name) {
        final Optional<Project> p = findProjectById(Integer.parseInt(id));
        if (!p.isPresent()) {
            return null;
        }
        final Project project = p.get();
        project.setName(name);
        HibernateUtil.execute(session -> {
            session.update(project);
            return null;
        });
        return project;
    }

    public void updateProjectState(int projectId, ProjectState state) {
        final Optional<Project> p = findProjectById(projectId);
        if (!p.isPresent()) {
            return;
        }
        final Project project = p.get();
        project.setProjectState(state);
        HibernateUtil.execute(session -> {
            session.update(project);
            return null;
        });
    }

    @Override
    public void setProjectError(int projectId, String message) {
        if(message == null){
            clearProjectError(projectId);
            return;
        }
        final Optional<Project> projectResult = findProjectById(projectId);
        if(!projectResult.isPresent()){
            return;
        }

        final Project project = projectResult.get();
        project.setProcessingError(message);
        HibernateUtil.execute(session -> {
            session.update(project);
            return null;
        });
    }

    @Override
    public void clearProjectError(int projectId) {
        setProjectError(projectId,"");
    }

    @Override
    public List<ProjectView> getProjects() {
        return getAll().stream().map(ProjectView::fromProject).collect(Collectors.toList());
    }

    @Override
    public List<Project> getUserProjects(String userId) {
       return HibernateUtil.execute(session ->  {
            return session.createQuery("select p from Project p where p.userId = :userId", Project.class)
                    .setParameter("userId", userId)
                    .list();

        });
    }

    @Override
    public Optional<Project> getProject(String projectId) {
        int id = Integer.parseInt(projectId);
        return findProjectById(id);
    }

    public boolean deleteProject(String name){
        Optional<Project> project = findProject(name);
        if(!project.isPresent())
            return false;

        return HibernateUtil.execute(session -> {
            session.delete(project.get());
            return true;
        });
    }

    public boolean deleteProjectById(String id){

        Optional<Project> project = findProjectById(Integer.parseInt(id));
        if(!project.isPresent())
            return false;


        return HibernateUtil.execute(session -> {
            session.delete(project.get());
            return true;
        });
    }

    public boolean canEdit(String projectId){
        int pId = Integer.parseInt(projectId);
        return HibernateUtil.execute(session -> {
            Optional<ProjectState> state =  (Optional<ProjectState>) session.createQuery("select p.projectState from Project p WHERE p.id = :pId")
                    .setParameter("pId", pId).uniqueResultOptional();

            return state.isPresent() && state.get() == ProjectState.IDLE;
        });
    }

    private Optional<Project> findProjectById(int id) {
        return HibernateUtil.execute(session ->  {
            return session.createQuery("select p from Project p where p.id = :id", Project.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();

        });
    }

    public Optional<Project> findProject(String name) {
        return HibernateUtil.execute(session ->  {
            return  session.createQuery("select p from Project p where p.name like :name", Project.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();

        });
    }

    public List<Project> getAll() {
       return HibernateUtil.execute((session -> {
           return session.createQuery("select p from Project p", Project.class).list();

       }));
    }

    public List<Result> getResults(String projectId)
    {
        int pId = Integer.parseInt(projectId);
        return HibernateUtil.execute((session -> {
            return  session.createQuery("select new de.unidisk.view.results.Result(t.topic.name, t.score, (select count(k.id) FROM KeyWordScore k where k.keyword.topicId = t.topic.id), t.searchMetaData.university )" +
                    " from TopicScore t WHERE t.topic.projectId = :pId", Result.class)
                    .setParameter("pId",pId).list();
        }));
    }

    @Override
    public List<Project> getProjects(ProjectState state){
        return HibernateUtil.execute((session -> {
            return session.createQuery("select p from Project p where p.projectState = :state", Project.class).setParameter("state",state).list();
        }));
    }
}
