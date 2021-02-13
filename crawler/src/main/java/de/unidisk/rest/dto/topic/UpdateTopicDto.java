package de.unidisk.rest.dto.topic;

public class UpdateTopicDto {

    String id;
    String name;

    public  UpdateTopicDto(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

