package de.unidisk.contracts.services.recommendation;

public class KeywordRecommendation {
    final double probability;
    final String keyword;

    public KeywordRecommendation(double probability, String keyword) {
        this.probability = probability;
        this.keyword = keyword;
    }

    public double getProbability() {
        return probability;
    }

    public String getKeyword() {
        return keyword;
    }
}
