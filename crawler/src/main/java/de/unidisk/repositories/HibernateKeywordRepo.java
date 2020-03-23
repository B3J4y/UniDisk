package de.unidisk.repositories;

import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.dao.KeywordDAO;
import de.unidisk.entities.hibernate.Keyword;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.Optional;
@ApplicationScoped
@ManagedBean(name = "keywordRepository")
public class HibernateKeywordRepo implements IKeywordRepository {

    @Override
    public Optional<Keyword> getKeyword(int keywordId) {
        return new KeywordDAO().get(keywordId);
    }
}
