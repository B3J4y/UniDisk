package de.unidisk.view;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "dtAddRowView")
@ViewScoped
public class AddRowView implements Serializable {

    private java.util.List<Project> projects;

    // here the input from new project is bound
    private String newProjectText;

    public AddRowView() {
    }

    public String getNewProjectText() {
        return newProjectText;
    }

    public void setNewProjectText(String newProjectText) {
        this.newProjectText = newProjectText;
    }

    @ManagedProperty("#{projectService}")
    private ProjectService service;

    @PostConstruct
    public void init() {
        // TODO @Jan :: populate projects with exisiting projects from db
        newProjectText = "neues Projekt";
        projects = service.getProjects(15);
    }

    public java.util.List<Project> getProjects() {
        return projects;
    }

    public java.util.List<String> getNames() {
        return service.getNames();
    }

    public ProjectService getService() {
        return service;
    }

    public void setService(ProjectService service) {
        this.service = service;
    }

    /* public void setService(CarService service) {
        this.service = service;
    }

    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Project Edited", ((Project) event.getObject()).getStatus());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Project) event.getObject()).getStatus());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowDelete(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Project Deleted", ((Project) event.getObject()).getStatus());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }*/

    public void onAddNew(String projectName) {
        // TODO @JAN sync projects with db
        System.out.println(projectName + " entered");
        projects.add(projects.get(0));
    }

    public void deleteRow(de.unidisk.view.Project project) {
        // TODO @JAN delete projects in db
        System.out.println("hi");
        projects.remove(project);

    }

}