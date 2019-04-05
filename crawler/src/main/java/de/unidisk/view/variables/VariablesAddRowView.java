package de.unidisk.view.variables;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "variablesAddRowView")
@ViewScoped
public class VariablesAddRowView implements Serializable {

    private java.util.List<Variable> variables;

    // here the input from new project is bound
    private String newProjectText;

    public VariablesAddRowView() {
    }

    public String getNewProjectText() {
        return newProjectText;
    }

    public void setNewProjectText(String newProjectText) {
        this.newProjectText = newProjectText;
    }

    @ManagedProperty("#{variableService}")
    private VariableService variableService;

    @PostConstruct
    public void init() {
        // TODO @Jan :: populate projects with exisiting projects from db
        newProjectText = "neues Projekt";
        variables = variableService.getVariables(15);
    }

    public java.util.List<Variable> getVariables() {
        return variables;
    }

    public java.util.List<String> getNames() {
        return variableService.getNames();
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
        variables.add(variables.get(0));
    }

    public void deleteRow(Variable project) {
        // TODO @JAN delete projects in db
        System.out.println("hi");
        variables.remove(project);

    }

    public void setVariableService(VariableService variableService) {
        this.variableService = variableService;
    }

    public VariableService getVariableService() {
        return variableService;
    }
}