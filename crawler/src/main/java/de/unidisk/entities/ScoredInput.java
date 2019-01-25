package de.unidisk.entities;

public interface ScoredInput {
    double getScore();

    void setScore(double score);

    void setSearchMetaData(SearchMetaData smd);

    SearchMetaData getSearchMetaData();

    Input getInput();

    default String getUniName() {
        return getSearchMetaData().getUniversity().getName();
    }

    default String getInputName() {
        return getInput().getName();
    }
}
