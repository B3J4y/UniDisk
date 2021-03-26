package de.unidisk.dao;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.repositories.params.project.UpdateProjectParams;
import de.unidisk.entities.hibernate.*;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.view.model.KeywordItem;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectDAO  implements IProjectRepository {

    public ProjectDAO() {
    }



    public Project createProject(CreateProjectParams params) throws DuplicateException {
        Project project;
        if(params.areSubprojectParams()){
            final Optional<Project> optionalParentProject = this.findProjectById(params.getParentProjectId());
            if (!optionalParentProject.isPresent()) {
                return null;
            }
            final Project parentProject = optionalParentProject.get();
            if(parentProject.isSubproject())
                throw new IllegalArgumentException("Unable to create subproject for subproject.");
            project = new Project();
            project.setParentProjectId(parentProject.getId());
            project.setUserId(parentProject.getUserId());
            project.setProjectSubtype(params.getProjectSubtype());
        }else {
            project = new Project(params.getName());
            project.setUserId(params.getUserId());
        }

        project.setProjectState(ProjectState.IDLE);


        return HibernateUtil.executeUpdate(session -> {
            session.save(project);
            project.setTopics(new ArrayList<>());
            return project;
        });
    }

    @Override
    public Project updateProject(UpdateProjectParams params) throws DuplicateException {
        final Optional<Project> p = findProjectById(Integer.parseInt(params.getProjectId()));
        if (!p.isPresent()) {
            return null;
        }
        final Project project = p.get();
        project.setName(params.getName());
        HibernateUtil.executeUpdate(session -> {
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
    public void rateTopicScore(String topicScoreId, ResultRelevance relevance) throws EntityNotFoundException {
        new TopicScoreDAO().rateScore(Integer.parseInt(topicScoreId),relevance);
    }

    @Override
    public List<Project> getSubprojects(String projectId) {
        return HibernateUtil.execute(session -> session.createQuery("select p from Project p " +
                "LEFT JOIN Topic t ON p.id = t.projectId " +
                "LEFT JOIN Keyword  k ON t.id = k.topicId " +
                "where p.parentProjectId = :id", Project.class)
                .setParameter("id", Integer.parseInt(projectId))
                .list());
    }

    @Override
    public Project generateSubprojectByCustom(String projectId) throws EntityNotFoundException, DuplicateException {

        final Project parentProject = this.getProjectDetailsOrFail(projectId);
        final DuplicateException[] duplicateException = new DuplicateException[1];

        final Project result = HibernateUtil.execute(session ->  {
            final Project subtypeProject;
            try {
                subtypeProject = this.createProject(CreateProjectParams.subproject(parentProject.getId(), ProjectSubtype.CUSTOM_ONLY));
            } catch (DuplicateException e) {
                e.printStackTrace();
                duplicateException[0] = e;
                return null;
            }
            final List<Topic> subtypeTopics = new ArrayList<>();
                parentProject.getTopics().forEach(topic -> {
                    final Topic t = new Topic();
                    t.setName(topic.getName());
                    t.setProjectId(subtypeProject.getId());
                    session.save(t);
                    final List<Keyword> keywords = new ArrayList<>();
                    t.setKeywords(keywords);
                    topic.getKeywords().forEach(keyword -> {
                        if(keyword.isSuggestion())
                            return;
                        final Keyword k = new Keyword();
                        k.setName(keyword.getName());
                        k.setTopicId(t.getId());
                        session.save(k);
                        keywords.add(k);
                    });
                    subtypeTopics.add(t);
                });
                subtypeProject.setTopics(subtypeTopics);
                session.update(subtypeProject);
                return subtypeProject;

        });
        if(duplicateException[0] != null)
                throw duplicateException[0];
        return result;
    }

    @Override
    public List<ProjectView> getProjects() {
        return getAll().stream().map(ProjectView::fromProject).collect(Collectors.toList());
    }

    @Override
    public List<Project> getUserProjects(String userId) {
       return HibernateUtil.execute(session ->  {
           // Projects with parentProjectId != null were created by the system and should not be visible to the user
            return session.createQuery("select p from Project p where p.userId = :userId AND p.parentProjectId IS null", Project.class)
                    .setParameter("userId", userId)
                    .list();

        });
    }

    @Override
    public Optional<Project> findUserProjectByName(String userId, String name) {
        return HibernateUtil.execute(session ->  {
            return session.createQuery("select p from Project p where p.userId = :userId AND p.name = :name", Project.class)
                    .setParameter("userId", userId)
                    .setParameter("name",name)
                    .uniqueResultOptional();

        });
    }

    @Override
    public Optional<Project> getProject(String projectId) {
        int id = Integer.parseInt(projectId);
        return findProjectById(id);
    }

    @Override
    public Optional<Project> getProjectDetails(String projectId) {
        return HibernateUtil.execute(session ->  {
            return session.createQuery("select p from Project p " +
                    "LEFT JOIN Topic t ON p.id = t.projectId " +
                    "LEFT JOIN Keyword  k ON t.id = k.topicId " +
                    "where p.id = :id", Project.class)
                    .setParameter("id", Integer.parseInt(projectId))
                    .uniqueResultOptional();

        });
    }

    @Override
    public Project getProjectDetailsOrFail(String projectId) throws EntityNotFoundException {
        final Optional<Project> optionalProject = this.getProjectDetails(projectId);
        if(!optionalProject.isPresent())
                throw new EntityNotFoundException(Project.class, Integer.parseInt(projectId));
        return optionalProject.get();
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
            return  session.createQuery("select new de.unidisk.view.results.Result(t.id, t.topic.id, t.topic.name, t.score, (select count(k.id) FROM KeyWordScore k where k.keyword.topicId = t.topic.id), t.searchMetaData.university, t.resultRelevance )" +
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
