package de.unidisk.view.variables;

import de.unidisk.view.project.Project;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "variablesView")
@SessionScoped
public class VariablesView {

    private String projectSelected;

    public String getProjectSelected() {
        return projectSelected;
    }

    public void setProjectSelected(String projectSelected) {
        this.projectSelected = projectSelected;
    }
}
