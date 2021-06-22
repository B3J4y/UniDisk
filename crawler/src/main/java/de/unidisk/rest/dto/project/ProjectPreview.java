package de.unidisk.rest.dto.project;

import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.Topic;

import java.util.List;

public class ProjectPreview {

    private String id;

    private String name;

    private ProjectState projectState;

    private String processingError;

    private List<Topic> topics;


    public static ProjectPreview fromEntity(Project project){
        final ProjectPreview preview = new ProjectPreview();
        preview.id = String.valueOf(project.getId());
        preview.name = project.getName();
        preview.processingError = project.getProcessingError();
        preview.projectState = project.getSubprojectState();
        preview.processingError = project.getProcessingError();
        preview.topics = project.getTopics();
        return preview;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public String getProcessingError() {
        return processingError;
    }

    public List<Topic> getTopics() {
        return topics;
    }
}
