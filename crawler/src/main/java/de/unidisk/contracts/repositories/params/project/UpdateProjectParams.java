package de.unidisk.contracts.repositories.params.project;

public class UpdateProjectParams {

    final String projectId;
    final String name;

    public UpdateProjectParams(String projectId, String name) {
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
