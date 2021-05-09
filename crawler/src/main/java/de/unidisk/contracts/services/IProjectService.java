package de.unidisk.contracts.services;

import de.unidisk.entities.hibernate.Project;

public interface IProjectService {

    boolean deleteProject(String projectId);

    Project getProject(String projectId);
}
