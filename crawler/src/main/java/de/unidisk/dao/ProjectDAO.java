package de.unidisk.dao;

import de.unidisk.common.ProjectResult;
import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.repositories.params.project.UpdateProjectParams;
import de.unidisk.entities.hibernate.*;
import de.unidisk.entities.results.KeywordResult;
import de.unidisk.entities.results.Result;
import de.unidisk.rest.dto.topic.RateTopicResultDto;
import org.hibernate.query.Query;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if(state == ProjectState.RUNNING)
            project.setProcessingHeartbeat(java.time.Instant.now());
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
    public void rateResult(RateTopicResultDto args) throws EntityNotFoundException {
        final int topicId = Integer.parseInt(args.getTopicId());

        HibernateUtil.execute(session -> {

            final Optional<ProjectRelevanceScore> existingScore = session.createQuery("Select score from ProjectRelevanceScore score " +
            "where score.topicId = :topicId and score.searchMetaData.url = :url"
            ).setParameter("topicId", topicId)
                    .setParameter("url", args.getUrl()).uniqueResultOptional();

            if(existingScore.isPresent()){
                final ProjectRelevanceScore score = existingScore.get();
                score.setResultRelevance(args.getRelevance());
                session.update(score);
                return null;
            }

            final List<SearchMetaData> searchMetaDataList = session.createQuery("" +
                    "Select smd from SearchMetaData  smd " +
                    "INNER JOIN KeyWordScore kws ON kws.searchMetaData.id = smd.id " +
                    "INNER JOIN Topic t ON t.id = kws.keyword.topicId " +
                            "where t.id = :topicId AND smd.url = :url",
                    SearchMetaData.class
            ).setParameter("topicId", topicId)
            .setParameter("url", args.getUrl()).list();

            if(searchMetaDataList.isEmpty())
                throw new IllegalArgumentException("No search metadata with with matching arguments found.");

            final SearchMetaData searchMetaData =searchMetaDataList.get(0);

            final ProjectRelevanceScore score = new ProjectRelevanceScore();
            score.setSearchMetaData(searchMetaData);
            score.setTopicId(topicId);
            score.setResultRelevance(args.getRelevance());
            session.save(score);
            return null;
        });
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
    public boolean projectFinishedProcessing(String projectId) {
        return HibernateUtil.execute(session -> {
            final Optional<Project> optionalProject = session.createQuery("select p from Project p " +
                    "where p.id = :id", Project.class)
                    .setParameter("id", Integer.parseInt(projectId))
                    .uniqueResultOptional();
            if(!optionalProject.isPresent())
                return false;

            final Project project = optionalProject.get();
            final Stream<Project> projectsStream = mergeProjectWithSubprojects(project).stream();

            final boolean finished = projectsStream.map(Project::finishedProcessing).reduce(true,Boolean::logicalAnd);
            return finished;
        });
    }

    @Override
    public void updateHeartbeat(String projectId) throws EntityNotFoundException {

        EntityNotFoundException exception = HibernateUtil.execute(session -> {

            final Optional<Project> p = this.getProject(projectId);
            if(!p.isPresent())
                return new EntityNotFoundException(Project.class,Integer.parseInt(projectId));

            final Project project = p.get();
            project.setProcessingHeartbeat(java.time.Instant.now());
            session.update(project);

            return null;
            });
        if(exception != null)
            throw exception;
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
            final Optional<Project> p =  session.createQuery("select p from Project p where p.id = :pId", Project.class)
                    .setParameter("pId",pId).uniqueResultOptional();
            if(!p.isPresent())
                return new ArrayList<>();

            final Project project = p.get();

            return project.getTopics().stream().flatMap(topic -> {
                final Map<Integer, List<KeyWordScore>> universityScores = new HashMap<>();
                topic.getKeywords().stream().flatMap(keyword -> keyword.getKeyWordScores().stream()).forEach(keyWordScore ->{
                    final int universityId = keyWordScore.getSearchMetaData().getUniversity().getId();
                    if(universityScores.containsKey(universityId)){
                        universityScores.get(universityId).add(keyWordScore);
                    }else {
                        final ArrayList<KeyWordScore> list = new ArrayList<>();
                        list.add(keyWordScore);
                        universityScores.put(universityId,list);
                    }
                });


                final List<Result> topicResults = topic.getTopicScores().stream().map(topicScore -> {
                    final int universityId = topicScore.getSearchMetaData().getUniversity().getId();
                    final List<KeyWordScore> scores = universityScores.get(universityId);
                    if(scores == null)
                        return null;

                    final List<KeywordResult> keywordResults = topic.getKeywords().stream().flatMap(keyword -> {
                        final Stream<KeyWordScore> keyWordScoreStream = scores.stream().filter(s -> s.getKeywordId() == keyword.getId());
                        return keyWordScoreStream.map(keyWordScore ->
                                new KeywordResult(
                                        keyword.getId(),
                                        keyword.getName(),
                                        keyWordScore.getSearchMetaData(),
                                        keyWordScore.getScore(),
                                        keyWordScore.getPageTitle()
                                )
                        );
                    }).collect(Collectors.toList());


                    return new Result(
                            topicScore.getId(),
                            topic.getId(),
                            topic.getName(),
                            topicScore.getScore(),
                            keywordResults.size(),
                            topicScore.getSearchMetaData().getUniversity(),
                            keywordResults
                    );
                }).filter(Objects::nonNull).collect(Collectors.toList());
                return topicResults.stream();
            }).collect(Collectors.toList());
        }));
    }

    public List<ProjectResult> getProjectResults(String projectId) {
        int pId = Integer.parseInt(projectId);
        final Optional<Project> optionalProject = this.findProjectById(pId);
        if (!optionalProject.isPresent())
            return Collections.emptyList();
        final Project project = optionalProject.get();

        final List<Project> projects = mergeProjectWithSubprojects(project);


        final List<ProjectResult> results = projects.parallelStream().map(proj -> {
            final List<Result> projectResult = this.getResults(String.valueOf(proj.getId()));
            final List<ProjectRelevanceScore> scores = proj.getTopics().stream().flatMap(topic -> topic.getRelevanceScores().stream()).collect(Collectors.toList());
            return new ProjectResult(projectResult, proj.getProjectSubtype(),scores);
        }).collect(Collectors.toList());
        return results;
    }

    private List<Project> mergeProjectWithSubprojects(Project project){
        final List<Project> projects = new ArrayList<Project>(Arrays.asList(project));
        projects.addAll(project.getSubprojects());
        return projects;
    }

    @Override
    public List<Project> getProjects(ProjectState state){
        return HibernateUtil.execute((session -> {
            return session.createQuery("select p from Project p where p.projectState = :state", Project.class).setParameter("state",state).list();
        }));
    }

    @Override
    public List<Project> getDeadProjects() {
        final java.time.Instant latestAllowedHeartbeat = getLatestAllowedHearbeat();
        return HibernateUtil.execute((session -> {
            Query query = session.createQuery("select p from Project p where p.projectState = :state and p.processingHeartbeat < :allowedHeartbeat", Project.class)
                    .setParameter("state",ProjectState.RUNNING)
                    .setParameter("allowedHeartbeat",latestAllowedHeartbeat);
            return query.list();
        }));
    }

    public void cleanDeadProjects() {
        final java.time.Instant latestAllowedHeartbeat = getLatestAllowedHearbeat();
        HibernateUtil.execute(session -> {

            Query keywordScoreQuery = session.createQuery("delete from KeyWordScore ks where ks.id in (" +
                    "Select ks.id from KeyWordScore ks " +
                            "where ks.keyword.finishedProcessingAt IS NULL " +
                            "and ks.keyword.topic.project.projectState = :state " +
                            "and ks.keyword.topic.project.processingHeartbeat < :allowedHeartbeat " +
                    ")")
                     .setParameter("state",ProjectState.RUNNING)
                    .setParameter("allowedHeartbeat",latestAllowedHeartbeat);

            Query topicScoreQuery = session.createQuery("delete from TopicScore ts where ts.id in (" +
                    "Select ts.id from TopicScore ts " +
                    "where ts.topic.finishedProcessingAt IS NULL " +
                    "and ts.topic.project.projectState = :state " +
                    "and ts.topic.project.processingHeartbeat < :allowedHeartbeat " +
                    ")")
                    .setParameter("state",ProjectState.RUNNING)
                    .setParameter("allowedHeartbeat",latestAllowedHeartbeat);

            keywordScoreQuery.executeUpdate();
            topicScoreQuery.executeUpdate();

            return null;
        });
    }

    private java.time.Instant getLatestAllowedHearbeat(){
        return java.time.Instant.now().minus(10, ChronoUnit.MINUTES);
    }
}
