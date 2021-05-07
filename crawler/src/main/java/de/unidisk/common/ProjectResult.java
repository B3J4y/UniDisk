package de.unidisk.common;

import de.unidisk.entities.hibernate.ProjectRelevanceScore;
import de.unidisk.entities.hibernate.ProjectSubtype;
import de.unidisk.view.results.Result;

import java.util.List;

public class ProjectResult {
    final List<Result> results;
    final ProjectSubtype projectSubtype;
    final List<ProjectRelevanceScore> relevanceScores;

    public ProjectResult(List<Result> results, ProjectSubtype subtype,List<ProjectRelevanceScore> relevanceScores) {
        this.results = results;
        this.projectSubtype = subtype;
        this.relevanceScores = relevanceScores;
    }

    public List<Result> getResults() {
        return results;
    }

    public ProjectSubtype getProjectSubtype() {
        return projectSubtype;
    }

    public List<ProjectRelevanceScore> getRelevanceScores() {
        return relevanceScores;
    }
}