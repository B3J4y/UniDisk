package de.unidisk.common;

import de.unidisk.entities.hibernate.ProjectSubtype;
import de.unidisk.view.results.Result;

import java.util.List;

public class ProjectResult {
    final List<Result> results;
    final ProjectSubtype projectSubtype;

    public ProjectResult(List<Result> results, ProjectSubtype subtype) {
        this.results = results;
        this.projectSubtype = subtype;
    }

    public List<Result> getResults() {
        return results;
    }

}