package de.unidisk.repositories.contracts;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.view.model.KeywordItem;
import de.unidisk.view.model.MapMarker;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;

import java.io.Serializable;
import java.util.List;

public interface IProjectRepository extends Serializable {

    List<ProjectView> getProjects();

    /**
     * Load the project with the given id.
     * @param projectId
     * @return Project or null if none with id exists
     */
    Project getProject(String projectId);
    List<KeywordItem> getProjectKeywords(String projectId);

    boolean deleteProject(String projectId);
    List<Result> getResults(String projectId);

    List<MapMarker> getMarker(String projectId);

    boolean canEdit(String projectId);
}
