package de.unidisk.services;

import de.unidisk.common.ProjectStateMapper;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.repositories.contracts.IProjectRepository;
import de.unidisk.view.project.ProjectView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.List;
import java.util.stream.Collectors;

@SessionScoped
@ManagedBean(name = "projectService")
public class ProjectService implements IProjectService {

    @ManagedProperty("#{projectRepository}")
    IProjectRepository projectRepository;
    public List<ProjectView> getProjects() {
        return projectRepository.getProjects();
    }

    @Override
    public Project getProject(String projectId) {
        return projectRepository.getProject(projectId);
    }


    public boolean deleteProject(String projectId){
        return projectRepository.deleteProject(projectId);
    }

    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
}
