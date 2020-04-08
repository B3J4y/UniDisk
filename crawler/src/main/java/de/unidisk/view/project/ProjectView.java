package de.unidisk.view.project;

import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Objects;

@ManagedBean
public class ProjectView implements Serializable {
    private String name;
    private ProjectState status;
    private String id;
    private boolean canEdit;
    private boolean canSetToIdle;

    public ProjectView(String name, ProjectState status, String id) {
        this.name = name;
        this.status = status;
        this.id = id;
        this.canEdit = status == ProjectState.IDLE;
        this.canSetToIdle = status == ProjectState.WAITING;
    }

    public static ProjectView fromProject(Project p){
        return new ProjectView(p.getName(),p.getProjectState(),String.valueOf(p.getId()));
    }

    public ProjectView() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectState getStatus() {
        return status;
    }

    public void setStatus(ProjectState status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectView project = (ProjectView) o;
        return Objects.equals(getName(), project.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }


    public boolean getCanEdit() {
        return canEdit;
    }

    public boolean isCanSetToIdle() {
        return canSetToIdle;
    }
}
