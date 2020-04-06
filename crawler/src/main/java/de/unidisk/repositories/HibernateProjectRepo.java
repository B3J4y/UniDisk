package de.unidisk.repositories;

import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.view.model.KeywordItem;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;


import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@ManagedBean(name = "projectRepository")
public class HibernateProjectRepo implements IProjectRepository {

    final ProjectDAO projectDAO = new ProjectDAO();

    @Override
    public List<ProjectView> getProjects() {
        List<ProjectView> projects = projectDAO.getAll().stream()
                .map(project -> new ProjectView(project.getName(), project.getStatus(), String.valueOf(project.getId())))
                .collect(Collectors.toList());
        return projects;
    }

    @Override
    public Project getProject(String projectId) {
        final Optional<Project> p = projectDAO.findProjectById(Integer.parseInt(projectId));
        return p.isPresent() ? p.get() : null;
    }

    @Override
    public List<KeywordItem> getProjectKeywords(String projectId) {
        final Project p = projectDAO.findProject(projectId).get();

        return p.getTopics().stream().map(( t) -> new KeywordItem(String.valueOf(t.getId()),t.getName(), t.getName() )).collect(Collectors.toList());
    }

    @Override
    public boolean deleteProject(String projectId) {
        return projectDAO.deleteProjectById(projectId);
    }

    @Override
    public List<Result> getResults(String projectId) {
        return projectDAO.getResults(projectId);
    }

    @Override
    public boolean canEdit(String projectId) {
        return projectDAO.canEdit(projectId);
    }

    @Override
    public List<Project> getProjects(ProjectState state) {
        return projectDAO.getProjects(state);
    }

    @Override
    public void updateProjectState(int projectId, ProjectState state) {
        projectDAO.updateProjectState(projectId,state);
    }

}
