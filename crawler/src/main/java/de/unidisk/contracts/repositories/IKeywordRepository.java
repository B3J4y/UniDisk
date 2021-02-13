package de.unidisk.contracts.repositories;


import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.keyword.UpdateKeywordParams;
import de.unidisk.entities.hibernate.Keyword;

import java.util.Optional;

public interface IKeywordRepository {


    Optional<Keyword> getKeyword(int keywordId);

    Keyword createKeyword(CreateKeywordParams params) throws DuplicateException;

    Keyword updateKeyword(UpdateKeywordParams params) throws DuplicateException;

    boolean deleteKeyword(int keywordId);
}
