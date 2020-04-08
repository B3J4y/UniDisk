package de.unidisk.contracts.services;

import de.unidisk.crawler.model.ScoreResult;

import java.util.List;

public interface IScoringService {

    List<ScoreResult> getKeywordScore(int projectId, int keywordId);

    List<ScoreResult> getTopicScores(int projectId, int topicId);
}
