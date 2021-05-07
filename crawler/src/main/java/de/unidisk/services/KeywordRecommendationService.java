package de.unidisk.services;

import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.KeywordRecommendation;
import de.unidisk.contracts.services.recommendation.RecommendationResult;

import java.util.ArrayList;
import java.util.List;


public class KeywordRecommendationService implements IKeywordRecommendationService {

    @Override
    public RecommendationResult getTopicRecommendations(String topic) {
        final List<KeywordRecommendation> recommendations = new ArrayList<>();
        recommendations.add(new KeywordRecommendation(1,topic+" 2"));
        recommendations.add(new KeywordRecommendation(.7,topic+" 4"));
        return new RecommendationResult(
                recommendations,
                "0"
        );
    }
}
