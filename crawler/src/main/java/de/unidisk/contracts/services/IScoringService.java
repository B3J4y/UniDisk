package de.unidisk.contracts.services;

import de.unidisk.crawler.model.ScoreResult;

public interface IScoringService {

    ScoreResult getKeywordScore(int projectId, int keywordId);

    ScoreResult getTopicScore(int projectId, int topicId);
}
