package de.unidisk.contracts.services.keyword;

import java.util.List;

public class KeywordRecommendationResponse {

    final List<KeywordRecommendation> recommendations;
    final String requestId;

    public KeywordRecommendationResponse(List<KeywordRecommendation> recommendations, String requestId) {
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

