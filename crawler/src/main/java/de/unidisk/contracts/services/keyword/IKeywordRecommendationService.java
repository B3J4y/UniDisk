package de.unidisk.contracts.services.keyword;

import java.util.List;

public interface IKeywordRecommendationService {

    KeywordRecommendationResponse getRecommendations(String topic, List<String> keywords);
}
