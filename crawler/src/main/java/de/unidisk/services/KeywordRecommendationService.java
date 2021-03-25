package de.unidisk.services;

import de.unidisk.contracts.services.recommendation.IKeywordRecommendationService;
import de.unidisk.contracts.services.recommendation.RecommendationResult;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.ArrayList;


public class KeywordRecommendationService implements IKeywordRecommendationService {

    @Override
    public RecommendationResult getTopicRecommendations(String topic) {
        return new RecommendationResult(
                new ArrayList<>(),
                ""
        );
    }
}
