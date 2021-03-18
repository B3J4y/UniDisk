package de.unidisk.contracts.repositories.params.project;

import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectSubtype;

public class CreateProjectParams {
    final String userId;
    final String name;
    final int parentProjectId;
    final ProjectSubtype projectSubtype;

    public CreateProjectParams(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.parentProjectId = -1;
        this.projectSubtype = null;
    }

    private CreateProjectParams(int parentProjectId, ProjectSubtype subtype) {
        this.parentProjectId = parentProjectId;
        this.projectSubtype = subtype;
        this.name = null;
        this.userId = null;
    }

    public static CreateProjectParams subproject(int parentProjectId, ProjectSubtype subtype) {
        return new CreateProjectParams(parentProjectId,subtype);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getParentProjectId() {
        return parentProjectId;
    }

    public ProjectSubtype getProjectSubtype() {
        return projectSubtype;
    }

    public boolean areSubprojectParams(){
        return this.parentProjectId != -1;
    }
}
