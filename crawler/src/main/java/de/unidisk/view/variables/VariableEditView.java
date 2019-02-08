package de.unidisk.view.variables;

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

@ManagedBean(name="variablesEditView")
@ViewScoped
public class VariableEditView implements Serializable {

    private List<Variable> cars1;
    private List<Variable> cars2;

    @ManagedProperty("#{variableService}")
    private VariableService service;

    @PostConstruct
    public void init() {
        cars1 = service.getVariables(10);
        cars2 = service.getVariables(10);
    }

    public List<Variable> getCars1() {
        return cars1;
    }

    public List<Variable> getCars2() {
        return cars2;
    }

    public List<String> getNames() {
        return service.getNames();
    }

    public List<String> getStatuss() {
        return service.getStatuss();
    }

    public void setService(VariableService service) {
        this.service = service;
    }

 /*   public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Project Edited", ((Variable) event.getObject()).getStatus());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Variable) event.getObject()).getStatus());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }*/
}
