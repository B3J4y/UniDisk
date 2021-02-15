package de.unidisk.contracts.repositories.params.project;

public class CreateProjectParams {
    final String userId;
    final String name;

    public CreateProjectParams(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }


}
