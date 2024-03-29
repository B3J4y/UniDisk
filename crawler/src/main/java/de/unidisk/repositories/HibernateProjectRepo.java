package de.unidisk.repositories;

import de.unidisk.common.ProjectResult;
import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.params.project.CreateProjectParams;
import de.unidisk.contracts.repositories.params.project.UpdateProjectParams;
import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.rest.dto.topic.RateTopicResultDto;

import java.util.List;
import java.util.Optional;


public class HibernateProjectRepo implements IProjectRepository {

    final ProjectDAO projectDAO = new ProjectDAO();

    @Override
    public List<Project> getUserProjects(String userId) {
        return projectDAO.getUserProjects(userId);
    }

    @Override
    public Optional<Project> findUserProjectByName(String userId, String name) {
        return projectDAO.findUserProjectByName(userId,name);
    }

    @Override
    public Optional<Project> getProject(String projectId) {
        return projectDAO.getProject(projectId);
    }

    @Override
    public Optional<Project> getProjectDetails(String projectId) {
        return projectDAO.getProjectDetails(projectId);
    }

    @Override
    public Project getProjectDetailsOrFail(String projectId) throws EntityNotFoundException {
        return projectDAO.getProjectDetailsOrFail(projectId);
    }

    @Override
    public Project createProject(CreateProjectParams params) throws DuplicateException {
        return this.projectDAO.createProject(params);
    }

    @Override
    public Project updateProject(UpdateProjectParams params) throws DuplicateException {
        return this.projectDAO.updateProject(params);
    }

    @Override
    public boolean deleteProject(String projectId) {
        return projectDAO.deleteProjectById(projectId);
    }

    @Override
    public List<ProjectResult> getProjectResults(String projectId) {
        return projectDAO.getProjectResults(projectId);
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
    public List<Project> getDeadProjects() {
        return projectDAO.getDeadProjects();
    }

    @Override
    public void updateProjectState(int projectId, ProjectState state) {
        projectDAO.updateProjectState(projectId,state);
    }

    @Override
    public void setProjectError(int projectId, String message) {
        projectDAO.setProjectError(projectId,message);
    }

    @Override
    public void clearProjectError(int projectId) {
        projectDAO.clearProjectError(projectId);
    }

    @Override
    public void rateResult(RateTopicResultDto args) throws EntityNotFoundException {
        projectDAO.rateResult(args);
    }

    @Override
    public List<Project> getSubprojects(String projectId) {
        return projectDAO.getSubprojects(projectId);
    }

    @Override
    public Project generateSubprojectByCustom(String projectId) throws DuplicateException, EntityNotFoundException {
        return projectDAO.generateSubprojectByCustom(projectId);
    }

    @Override
    public boolean projectFinishedProcessing(String projectId) {
        return projectDAO.projectFinishedProcessing(projectId);
    }

    @Override
    public void updateHeartbeat(String projectId) throws EntityNotFoundException {
        projectDAO.updateHeartbeat(projectId);
    }

    @Override
    public void cleanDeadProjects() {
        projectDAO.cleanDeadProjects();
    }

}
