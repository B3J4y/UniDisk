package de.unidisk.view.project;

import de.unidisk.dao.ProjectDAO;
import de.unidisk.services.ProjectService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "dtAddKeywordRowView")
@ViewScoped
public class AddKeywordRowView implements Serializable {

    private java.util.List<ProjectView> projects;

    // here the input from new project is bound
    private String newProjectText;

    public AddKeywordRowView() {
    }

    @ManagedProperty("#{projectService}")
    private ProjectService service;

    @PostConstruct
    public void init() {
        // TODO @Jan :: populate projects with exisiting projects from db

        projects = service.getProjects();
    }

    public void refresh(){
        projects = service.getProjects();
    }

    public java.util.List<ProjectView> getProjects() {
        return projects;
    }

    public ProjectService getService() {
        return service;
    }

    public void setService(ProjectService service) {
        this.service = service;
    }

    public void onAddNew(String projectName) {

        ProjectDAO projectDAO = new ProjectDAO();
        projectDAO.createProject(projectName);
        de.unidisk.entities.hibernate.Project project = projectDAO.findProject(projectName).orElseThrow(IllegalStateException::new);
        projects.add(new ProjectView(project.getName(), project.getStatus(), String.valueOf(project.getId())));

        // TODO @JAN sync projects with db
        System.out.println(projectName + " entered");
        //projects.add(projects.get(0));
    }

    public void deleteRow(ProjectView project) {
        // TODO @JAN delete projects in db
        System.out.println("hi");
        projects.remove(project);

    }

}