package de.unidisk.services;

import de.unidisk.contracts.services.recommendation.RecommendationResult;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
public class KeywordRecommendationTest {

    @Test
    public void getSimiliar(){
        KeywordRecommendationService service = new KeywordRecommendationService();
        RecommendationResult result = service.getTopicRecommendations("wirtschaft");
        assertNotNull(result);
    }
}
