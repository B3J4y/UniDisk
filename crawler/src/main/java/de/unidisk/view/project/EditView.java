package de.unidisk.view.project;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

import java.util.List;

@ManagedBean(name="dtEditView")
@ViewScoped
public class EditView implements Serializable {

    private List<Project> cars1;
    private List<Project> cars2;

    @ManagedProperty("#{projectService}")
    private ProjectService service;

    @PostConstruct
    public void init() {
        cars1 = service.getProjects(10);
        cars2 = service.getProjects(10);
    }

    public List<Project> getCars1() {
        return cars1;
    }

    public List<Project> getCars2() {
        return cars2;
    }

    public java.util.List<String> getNames() {
        return service.getNames();
    }

    public java.util.List<String> getStatuss() {
        return service.getStatuss();
    }

    public void setService(ProjectService service) {
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

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
}
