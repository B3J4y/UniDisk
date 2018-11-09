package de.unidisk;

import de.unidisk.entities.KeyWordScore;
import de.unidisk.entities.TopicScore;

import java.util.List;

public class DummyScoreManager implements IScores {
    @Override
    public List<KeyWordScore> getKeyWordScores() {
        return null;
    }

    @Override
    public List<TopicScore> getTopicScores() {
        return null;
    }
}
