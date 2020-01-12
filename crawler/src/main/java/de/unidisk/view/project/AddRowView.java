package de.unidisk.view.project;

import de.unidisk.MessagingCenter;
import de.unidisk.common.ProjectStateMapper;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.services.ProjectService;
import de.unidisk.view.model.KeywordItem;
import org.primefaces.PrimeFaces;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;


@ManagedBean(name = AddRowView.BEAN_NAME)
@ViewScoped
public class AddRowView implements Serializable {
    public static final String BEAN_NAME = "dtAddRowView";

    private java.util.List<ProjectView> projects;
    private String deletionError;

    public AddRowView() {
    }

    @ManagedProperty("#{projectService}")
    private ProjectService service;

    @PostConstruct
    public void init() {

        projects = service.getProjects();
        MessagingCenter.getInstance().subscribe(this,"NewProject",(Project newProject) -> {
            System.out.println("AddRowView: new project created");
            projects.add(
                    new ProjectView(
                            newProject.getName(),
                            newProject.getStatus(),
                            String.valueOf(newProject.getId())
                    )
            );



            return null;
        });

        MessagingCenter.getInstance().subscribe(this,"ProjectDeleted",(String projectName) -> {
            System.out.println("AddRowView: project deleted");
            projects.removeIf((p) -> p.getName().equals(projectName));



            return null;
        });
    }

    @PreDestroy
    public void destroy() {
        MessagingCenter.getInstance().unsubscribe(this,"NewProject");
    }

    public void refresh(){
        projects = service.getProjects();
        if(projects.size() > 0){

        }
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

    public void deleteRow(ProjectView project) {
        boolean projectDeleted = new ProjectDAO().deleteProject(project.getName());
        if(projectDeleted) {
            deletionError = null;
            projects.remove(project);
            MessagingCenter.getInstance().send("ProjectDeleted", project.getName());
        }else{
            deletionError = "Projekt konnte nicht gelöscht werden.";
        }
    }

}