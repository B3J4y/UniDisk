package de.unidisk.view.variables;

import de.unidisk.entities.hibernate.Project;
import de.unidisk.contracts.repositories.IProjectRepository;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.Optional;

@ManagedBean(name = "variablesView")
@ViewScoped
public class VariablesView {

    @ManagedProperty("#{projectRepository}")
    private IProjectRepository service;

    private String projectId;

    //determines layout
    private ProjectViews view = ProjectViews.Default;
    private Project project;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {

        System.out.print("set project id to " + projectId);
        this.projectId = projectId;
    }

    public void load(){
        final Optional<Project> p = service.getProject(projectId);
        p.ifPresent(value -> project = value);
    }
    private String projectSelected = "test";

    public String getProjectSelected() {
        return project != null ? project.getName() : "Unbekanntes Projekt";
    }

    public ProjectViews getView() {
        return view;
    }

    public void setView(ProjectViews view) {
        if(view == null)
            this.view = ProjectViews.Default;
        else
        this.view = view;
    }

    public void showTopics(){

        this.view = ProjectViews.Topics;

    }

    public void showResults(){
        this.view = ProjectViews.Results;

    }

    public void showVisual(){
        this.view = ProjectViews.Visual;

    }

    public void setService(IProjectRepository service) {
        this.service = service;
    }
}
