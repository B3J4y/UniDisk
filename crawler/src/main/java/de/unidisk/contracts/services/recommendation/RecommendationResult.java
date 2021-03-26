package de.unidisk.contracts.services.recommendation;

import java.util.List;

public class RecommendationResult {
    final List<KeywordRecommendation> recommendations;
    final String requestId;

    public RecommendationResult(List<KeywordRecommendation> recommendations, String requestId) {
        this.recommendations = recommendations;
        this.requestId = requestId;
    }

    public List<KeywordRecommendation> getRecommendations() {
        return recommendations;
    }

    public String getRequestId() {
        return requestId;
    }
}
