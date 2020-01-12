package de.unidisk.view.project.dialog;

import de.unidisk.MessagingCenter;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.services.ProjectService;
import de.unidisk.view.project.ProjectView;
import org.primefaces.PrimeFaces;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "addProjectDialog")
@ViewScoped
public class AddProjectDialog implements Serializable {

    //name of new project
    private String newProjectText;
    //error message that will be displayed to user
    private String errorText;

    public AddProjectDialog() {
    }


    @ManagedProperty("#{projectService}")
    private ProjectService service;

    @PostConstruct
    public void init() {

    }

    public String getErrorText() {
        return errorText;
    }

    public void setService(ProjectService service) {
        this.service = service;
    }

    public void reset(){
        newProjectText = null;
        errorText = null;
    }

    public void onAddNew(String projectName) {
        if(projectName == null || projectName.trim().equals("")){
            errorText = "Projektname darf nicht leer sein.";
            return;
        }else {
            try{
                boolean projectExists = new ProjectDAO().findProject(projectName).isPresent();
                if(projectExists) {
                    errorText = "Projektname existiert bereits";
                }else{
                    ProjectDAO projectDAO = new ProjectDAO();
                    projectDAO.createProject(projectName);
                    de.unidisk.entities.hibernate.Project project = projectDAO.findProject(projectName).orElseThrow(IllegalStateException::new);

                    PrimeFaces current = PrimeFaces.current();
                    current.executeScript("PF('dlg2').hide();");
                    newProjectText = null;
                    errorText = null;
                    System.out.println("Created project with name " + projectName);
                    MessagingCenter.getInstance().send("NewProject",project);
                }

            }catch(Exception e){
                errorText = "Unbekannter Fehler aufgetreten.";
                e.printStackTrace();
            }
        }
    }

    public String getNewProjectText() {
        return newProjectText;
    }

    public void setNewProjectText(String newProjectText) {
        this.newProjectText = newProjectText;
    }
}