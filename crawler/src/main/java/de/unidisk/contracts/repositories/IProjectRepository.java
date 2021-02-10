package de.unidisk.contracts.repositories;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface IProjectRepository extends Serializable {

    class CreateProjectArgs {
        final String userId;
        final String name;

        public CreateProjectArgs(String userId, String name) {
            this.userId = userId;
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }
    }

    class UpdateProjectArgs {
        final String projectId;
        final String name;

        public UpdateProjectArgs(String projectId, String name) {
            this.projectId = projectId;
            this.name = name;
        }

        public String getProjectId() {
            return projectId;
        }

        public String getName() {
            return name;
        }
    }

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

    Project createProject(CreateProjectArgs args) throws DuplicateException;
    Project updateProject(UpdateProjectArgs args) throws DuplicateException;
    boolean deleteProject(String projectId);


    List<Result> getResults(String projectId);

    boolean canEdit(String projectId);

    List<Project> getProjects(ProjectState state);

    void updateProjectState(int projectId, ProjectState state);
    void setProjectError(int projectId, String message);
    void clearProjectError(int projectId);
}
