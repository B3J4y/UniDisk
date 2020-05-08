package de.unidisk.view.project;

import de.unidisk.view.MessagingCenter;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.services.ProjectService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@ManagedBean(name = ProjectTable.BEAN_NAME)
@ViewScoped
public class ProjectTable implements Serializable {
    public static final String BEAN_NAME = "dtAddRowView";

    private List<ProjectState> projectStates;
    private java.util.List<ProjectView> projects;
    private String deletionError;
    private int projectReleaseId;

    public ProjectTable() {
    }

    @ManagedProperty("#{projectService}")
    private ProjectService service;

    @PostConstruct
    public void init() {
        projectStates = Arrays.asList(ProjectState.values());
        projects = service.getProjects();
        MessagingCenter.getInstance().subscribe(this,"NewProject",(Project newProject) -> {
            projects.add(
                    new ProjectView(
                            newProject.getName(),
                            newProject.getProjectState(),
                            String.valueOf(newProject.getId())
                    )
            );
            return null;
        });

        MessagingCenter.getInstance().subscribe(this,"ProjectDeleted",(String projectName) -> {
            projects.removeIf((p) -> p.getName().equals(projectName));
            return null;
        });
    }

    @PreDestroy
    public void destroy() {
        MessagingCenter.getInstance().unsubscribe(this,"NewProject");
        MessagingCenter.getInstance().unsubscribe(this,"ProjectDeleted");
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

    public List<ProjectState> getProjectStates() {
        return projectStates;
    }

    public List<ProjectView> getProjectOfState(ProjectState state){
        return projects.stream().filter(p -> p.getStatus() == state).collect(Collectors.toList());
    }

    public void setWaiting(int projectId){
        try{
            new ProjectDAO().updateProjectState(projectId, ProjectState.WAITING);

            updateProjectState(projectId, ProjectState.WAITING);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateProjectState(int projectId, ProjectState state){
        final Optional<ProjectView> p = this.projects.stream().filter((project) -> project.getId().equals(String.valueOf(projectId))).findFirst();
        if(!p.isPresent())
            return;
        p.get().setStatus(state);
    }
    public void setIdle(int projectId){
        try{
            new ProjectDAO().updateProjectState(projectId, ProjectState.IDLE);
            updateProjectState(projectId, ProjectState.IDLE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getProjectReleaseId() {
        return projectReleaseId;
    }

    public void setProjectReleaseId(int projectReleaseId) {
        this.projectReleaseId = projectReleaseId;
    }
}