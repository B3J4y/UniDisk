package de.unidisk.rest.dto.topic;

public class CreateTopicDto {

    String projectId;
    String name;

    public  CreateTopicDto(){}

    public CreateTopicDto(String projectId, String name) {
        this.projectId = projectId;
        this.name = name;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
