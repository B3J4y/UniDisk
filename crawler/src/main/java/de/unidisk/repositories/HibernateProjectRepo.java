package de.unidisk.repositories;

import de.unidisk.dao.KeywordDAO;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.KeyWordScore;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.University;
import de.unidisk.repositories.contracts.IProjectRepository;
import de.unidisk.view.model.KeywordItem;
import de.unidisk.view.model.MapMarker;
import de.unidisk.view.project.ProjectView;
import de.unidisk.view.results.Result;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SessionScoped
@ManagedBean(name = "projectRepository")
public class HibernateProjectRepo implements IProjectRepository {

    @Override
    public List<ProjectView> getProjects() {
        ProjectDAO projectDAO = new ProjectDAO();
        List<ProjectView> projects = projectDAO.getAll().stream()
                .map(project -> new ProjectView(project.getName(), project.getStatus(), String.valueOf(project.getId())))
                .collect(Collectors.toList());
        return projects;
    }

    @Override
    public Project getProject(String projectId) {
        final Optional<Project> p = new ProjectDAO().findProjectById(Integer.parseInt(projectId));
        return p.isPresent() ? p.get() : null;
    }

    @Override
    public List<KeywordItem> getProjectKeywords(String projectId) {
        final Project p = new ProjectDAO().findProject(projectId).get();

        return p.getTopics().stream().map(( t) -> new KeywordItem(String.valueOf(t.getId()),t.getName(), t.getName() )).collect(Collectors.toList());
    }

    @Override
    public boolean deleteProject(String projectId) {
        return new ProjectDAO().deleteProjectById(projectId);
    }

    @Override
    public List<Result> getResults(String projectId) {
        return new ProjectDAO().getResults(projectId).stream().map(this::mapKeywordScoreToResult).collect(Collectors.toList());
    }

    private Result mapKeywordScoreToResult(KeyWordScore r){
       return new Result(r.getUniName(),r.getScore(),r.getId());
    }

    @Override
    public List<MapMarker> getMarker(String projectId) {
        return new ProjectDAO().getMapMarker(projectId);
    }

    @Override
    public boolean canEdit(String projectId) {
        return new ProjectDAO().canEdit(projectId);
    }

}
