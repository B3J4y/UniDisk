package de.unidisk.entities.hibernate;

public interface ScoredInput {
    double getScore();

    void setScore(double score);

    void setSearchMetaData(SearchMetaData smd);

    SearchMetaData getSearchMetaData();

    Input getInput();

    default String getUniName() {
        final SearchMetaData searchMetaData = getSearchMetaData();
        if(searchMetaData == null)
                return null;

        return searchMetaData.getUniversity().getName();
    }


}
