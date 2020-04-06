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
        Project project = new Project(name);
        project.setProjectState(ProjectState.IDLE);
        return  HibernateUtil.execute(session -> {
            session.save(project);
            return project;
        });
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
    public List<ProjectView> getProjects() {
        return getAll().stream().map(ProjectView::fromProject).collect(Collectors.toList());
    }

    @Override
    public Project getProject(String projectId) {
        int id = Integer.parseInt(projectId);
        final Optional<Project> p  = findProjectById(id);
        return p.orElse(null);
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
            Query stateQuery = session.createQuery("select p.projectState from Project p WHERE p.id = :pId")
                    .setParameter("pId", pId);
            Optional<ProjectState> state = (Optional<ProjectState>) stateQuery.uniqueResultOptional();
            return state.isPresent() ? state.get() == ProjectState.IDLE : false;
        });
    }

    public Optional<Project> findProjectById(int id) {
        return HibernateUtil.execute(session ->  {
            Optional<Project> optProj = session.createQuery("select p from Project p where p.id = :id", Project.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();
            return optProj;
        });
    }

    public Optional<Project> findProject(String name) {
        return HibernateUtil.execute(session ->  {
            Optional<Project> optProj = session.createQuery("select p from Project p where p.name like :name", Project.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
            return optProj;
        });
    }

    public List<Project> getAll() {
       return HibernateUtil.execute((session -> {
           List<Project> project = session.createQuery("select p from Project p", Project.class).list();
           return project;
       }));
    }

    public List<Result> getResults(String projectId)
    {
        int pId = Integer.parseInt(projectId);
        return HibernateUtil.execute((session -> {
            List<Result> scores = session.createQuery("select new de.unidisk.view.results.Result(t.topic.name, t.score, (select count(k.id) FROM KeyWordScore k where k.keyword.topicId = t.topic.id), t.searchMetaData.university )" +
                    " from TopicScore t WHERE t.topic.projectId = :pId", Result.class)
                    .setParameter("pId",pId).list();
            return scores;
        }));
    }

    @Override
    public List<Project> getProjects(ProjectState state){
        return HibernateUtil.execute((session -> {
            List<Project> project = session.createQuery("select p from Project p where p.projectState = :state", Project.class).setParameter("state",state).list();
            return project;
        }));
    }
}
