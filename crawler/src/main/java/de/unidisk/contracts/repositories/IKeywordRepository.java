package de.unidisk.contracts.repositories;


import de.unidisk.entities.hibernate.Keyword;

import java.util.Optional;

public interface IKeywordRepository {

    Optional<Keyword> getKeyword(int keywordId);
}
