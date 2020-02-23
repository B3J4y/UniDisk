package de.unidisk.contracts.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.crawler.model.ScoreResult;

import java.net.MalformedURLException;

public interface IResultService {

    void CreateKeywordScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException;

    void CreateTopicScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException;
}
