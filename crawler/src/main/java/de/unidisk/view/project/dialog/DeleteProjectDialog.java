package de.unidisk.view.project.dialog;

import de.unidisk.MessagingCenter;
import de.unidisk.services.ProjectService;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "deleteProjectDialog")
@ViewScoped
public class DeleteProjectDialog {

    private String projectName;
    private String projectId;
    private String error;

    @ManagedProperty("#{projectService}")
    private ProjectService service;

    public void setup(String projectName, String projectId){
        this.projectId = projectId;
        this.projectName = projectName;
        System.out.println("setup project: " + projectName + " " + projectId);
    }

    public void delete(){
        if(projectId == null)
        {
            error = "Kein Projekt ausgewählt.";
        }else{
            final boolean deleted = service.deleteProject(this.projectId);
            if(!deleted)
                error = "Projekt konnte nicht gelöscht werden.";
            else{
                MessagingCenter.getInstance().send("ProjectDeleted",projectName);
                PrimeFaces current = PrimeFaces.current();
                current.executeScript("PF('projectDeleteDialog').hide();");
            }
        }
    }
    public String getProjectName() {
        return projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getError() {
        return error;
    }

    public void setService(ProjectService service) {
        this.service = service;
    }
}
