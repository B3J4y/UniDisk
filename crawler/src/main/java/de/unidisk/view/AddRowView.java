package de.unidisk.view;

import org.primefaces.event.RowEditEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;


@ManagedBean(name = "dtAddRowView")
@ViewScoped
public class AddRowView implements Serializable {

    private java.util.List<Project> projects;

    private String newProjectText;

    public String getNewProjectText() {
        return newProjectText;
    }

    public void setNewProjectText(String newProjectText) {
        this.newProjectText = newProjectText;
    }

    @ManagedProperty("#{carService}")
    private CarService service;

    @PostConstruct
    public void init() {
        newProjectText = "neues Projekt";
        projects = service.createCars(15);
    }

    public java.util.List<Project> getProjects() {
        return projects;
    }

    public java.util.List<String> getNames() {
        return service.getNames();
    }


    public void setService(CarService service) {
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
    }

    public void onAddNew(String projectName) {
        System.out.println(projectName + " entered");
        projects.add(projects.get(0));
    }

    public void deleteRow(de.unidisk.view.Project project) {
        System.out.println("hi");
        projects.remove(project);

    }

}