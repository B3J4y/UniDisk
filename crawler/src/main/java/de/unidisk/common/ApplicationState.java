package de.unidisk.common;

import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.University;

import java.util.List;

public class ApplicationState {

    private List<Project> projectList;
    private List<University> universities;

    public ApplicationState(List<Project> projectList, List<University> universities) {
        this.projectList = projectList;
        this.universities = universities;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public List<University> getUniversities() {
        return universities;
    }
}
