package de.unidisk.solr.services;

import de.unidisk.contracts.services.IScoringService;
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
