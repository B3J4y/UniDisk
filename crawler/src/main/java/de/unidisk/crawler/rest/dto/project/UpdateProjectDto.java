package de.unidisk.crawler.rest.dto.project;

import de.unidisk.entities.hibernate.ProjectState;

public class UpdateProjectDto {

    String name;


    public UpdateProjectDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
