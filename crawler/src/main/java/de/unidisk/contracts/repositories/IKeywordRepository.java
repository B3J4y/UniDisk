package de.unidisk.contracts.repositories;


import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.entities.hibernate.Keyword;

import java.util.Optional;

public interface IKeywordRepository {

    class CreateKeywordArgs {

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

    class UpdateKeywordArgs {

        String name;
        String keywordId;

        public UpdateKeywordArgs(String name, String keywordId) {
            this.name = name;
            this.keywordId = keywordId;
        }

        public String getName() {
            return name;
        }

        public String getKeywordId() {
            return keywordId;
        }
    }

    Optional<Keyword> getKeyword(int keywordId);

    Keyword createKeyword(CreateKeywordArgs args) throws DuplicateException;

    Keyword updateKeyword(UpdateKeywordArgs args) throws DuplicateException;

    boolean deleteKeyword(int keywordId);
}
