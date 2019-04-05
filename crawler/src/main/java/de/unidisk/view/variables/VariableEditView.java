package de.unidisk.view.variables;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "variablesEditView")
@ViewScoped
public class VariableEditView implements Serializable {

    private List<Variable> cars1;
    private List<Variable> cars2;

    @ManagedProperty("#{variableService}")
    private VariableService variableService;

    @PostConstruct
    public void init() {
        cars1 = variableService.getVariables(10);
        cars2 = variableService.getVariables(10);
    }

    public List<Variable> getCars1() {
        return cars1;
    }

    public List<Variable> getCars2() {
        return cars2;
    }

    public List<String> getNames() {
        return variableService.getNames();
    }

    public List<String> getKeywordss() {
        return variableService.getKeywordss();
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

    public VariableService getVariableService() {
        return variableService;
    }

    public void setVariableService(VariableService variableService) {
        this.variableService = variableService;
    }
}
