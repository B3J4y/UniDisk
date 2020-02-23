package de.unidisk.contracts.services;

import de.unidisk.entities.hibernate.Project;
import de.unidisk.view.project.ProjectView;

import java.util.List;

public interface IProjectService {

    boolean deleteProject(String projectId);

    List<ProjectView> getProjects();

    Project getProject(String projectId);
}
