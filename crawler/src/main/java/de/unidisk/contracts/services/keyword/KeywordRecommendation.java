package de.unidisk.contracts.services.keyword;

public class KeywordRecommendation {
    final double score;
    final String keyword;

    public KeywordRecommendation(double score, String keyword) {
        this.score = score;
        this.keyword = keyword;
    }

    public double getScore() {
        return score;
    }

    public String getKeyword() {
        return keyword;
    }
}
