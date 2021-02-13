package de.unidisk.rest.dto.project;

public class CreateProjectDto {

    String name;

    public CreateProjectDto() {
    }

    public CreateProjectDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
