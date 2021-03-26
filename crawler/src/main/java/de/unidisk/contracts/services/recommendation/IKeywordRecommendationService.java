package de.unidisk.contracts.services.recommendation;

public interface IKeywordRecommendationService {

    RecommendationResult getTopicRecommendations(String topic);
}
