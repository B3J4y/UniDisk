package de.unidisk.contracts.repositories;


import de.unidisk.entities.hibernate.Keyword;

import java.util.Optional;

public interface IKeywordRepository {

    public static class CreateKeywordArgs {

        String name;
        String topicId;

        public CreateKeywordArgs(String name, String topicId) {
            this.name = name;
            this.topicId = topicId;
        }

        public String getName() {
            return name;
        }

        public String getTopicId() {
            return topicId;
        }
    }

    Optional<Keyword> getKeyword(int keywordId);

    Keyword createKeyword(CreateKeywordArgs args);
}
