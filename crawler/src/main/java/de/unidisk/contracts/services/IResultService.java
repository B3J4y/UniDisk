package de.unidisk.contracts.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.crawler.model.ScoreResult;

import java.net.MalformedURLException;

public interface IResultService {

    void createKeywordScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException;

    void createTopicScore(ScoreResult result) throws EntityNotFoundException, MalformedURLException;
}
