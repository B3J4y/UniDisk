package de.unidisk.contracts.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.crawler.model.ScoreResult;

import java.util.List;

public interface IScoringService {

    List<ScoreResult> getKeywordScores(int projectId, int keywordId);

    List<ScoreResult> getTopicScores(int projectId, int topicId) throws EntityNotFoundException;

    List<ScoreResult> filterRelevantKeywordScores(List<ScoreResult> keywordScores);

    boolean canEvaluate();
}
