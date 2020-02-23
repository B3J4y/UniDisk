package de.unidisk.crawler.services;

import de.unidisk.crawler.contracts.IScoringService;
import de.unidisk.crawler.model.ScoreResult;

public class SolrScoringService implements IScoringService {
    @Override
    public ScoreResult getKeywordScore(int projectId, int keywordId) {
        return null;
    }

    @Override
    public ScoreResult getTopicScore(int projectId, int topicId) {
        return null;
    }
}
