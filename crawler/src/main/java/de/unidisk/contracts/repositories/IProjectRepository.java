package de.unidisk.contracts.repositories;

import de.unidisk.common.ProjectResult;
import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.repositories.params.project.UpdateProjectParams;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.ResultRelevance;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface IProjectRepository extends Serializable {

    List<ProjectView> getProjects();

    List<Project> getUserProjects(String userId);
    Optional<Project> findUserProjectByName(String userId, String name);

    /**
     * Load the project with the given id.
     * @param projectId
     * @return
     */
    Optional<Project> getProject(String projectId);

    /**
     * Loads project details of project with given id.
     * @param projectId id of project
     * @return Optional project with loaded topics and keywords. Value is present
     * if project with id exists.
     */
    Optional<Project> getProjectDetails(String projectId);
    /**
     * Loads project details of project with given id.
     * @param projectId id of project
     * @return project with loaded topics and keywords
     * @throws EntityNotFoundException if project with id doesn't exist
     */
    Project getProjectDetailsOrFail(String projectId) throws EntityNotFoundException;

    Project createProject(CreateProjectParams params) throws DuplicateException;
    Project updateProject(UpdateProjectParams params) throws DuplicateException;
    boolean deleteProject(String projectId);


    List<Result> getResults(String projectId);
    List<ProjectResult> getAllResults(String projectId);

    boolean canEdit(String projectId);

    List<Project> getProjects(ProjectState state);

    void updateProjectState(int projectId, ProjectState state);
    void setProjectError(int projectId, String message);
    void clearProjectError(int projectId);

    void rateTopicScore(String topicScoreId, ResultRelevance relevance) throws EntityNotFoundException;

    List<Project> getSubprojects(String projectId);
    Project generateSubprojectByCustom(String projectId) throws EntityNotFoundException, DuplicateException;
    boolean projectFinishedProcessing(String projectId);
}
