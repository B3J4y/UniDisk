package de.unidisk.dao;

import de.unidisk.entities.hibernate.SearchMetaData;

public class Score {

    private double score;
    private int universityId;


    public Score(double score, int universityId) {
        this.score = score;
        this.universityId = universityId;
    }

    public double getScore() {
        return score;
    }

    public int getUniversityId() {
        return universityId;
    }
}
