package de.unidisk.repositories;

import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.entities.hibernate.Keyword;

import java.util.Optional;

public class HibernateKeywordRepo implements IKeywordRepository {

    @Override
    public Optional<Keyword> getKeyword(int keywordId) {
        return new KeywordDAO().get(keywordId);
    }
}
