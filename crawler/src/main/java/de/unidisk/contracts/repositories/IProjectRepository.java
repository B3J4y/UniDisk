package de.unidisk.contracts.repositories;

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
    Optional<Project> getProjectDetails(String projectId);

    Project createProject(CreateProjectParams params) throws DuplicateException;
    Project updateProject(UpdateProjectParams params) throws DuplicateException;
    boolean deleteProject(String projectId);


    List<Result> getResults(String projectId);

    boolean canEdit(String projectId);

    List<Project> getProjects(ProjectState state);

    void updateProjectState(int projectId, ProjectState state);
    void setProjectError(int projectId, String message);
    void clearProjectError(int projectId);

    void rateTopicScore(String topicScoreId, ResultRelevance relevance) throws EntityNotFoundException;
}
