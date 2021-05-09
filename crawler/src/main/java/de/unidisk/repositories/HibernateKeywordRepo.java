package de.unidisk.repositories;

import de.unidisk.contracts.exceptions.DuplicateException;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.params.keyword.CreateKeywordParams;
import de.unidisk.contracts.repositories.params.keyword.UpdateKeywordParams;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.entities.hibernate.Keyword;

import java.util.Optional;

public class HibernateKeywordRepo implements IKeywordRepository {

    KeywordDAO dao = new KeywordDAO();

    @Override
    public Optional<Keyword> getKeyword(int keywordId) {
        return dao.get(keywordId);
    }

    @Override
    public Keyword createKeyword(CreateKeywordParams params) {
        return dao.createKeyword(params);
    }

    @Override
    public Keyword updateKeyword(UpdateKeywordParams params) throws DuplicateException {
        return dao.updateKeyword(params);
    }

    @Override
    public boolean deleteKeyword(int keywordId) {
        return dao.deleteKeyword(keywordId);
    }
}
