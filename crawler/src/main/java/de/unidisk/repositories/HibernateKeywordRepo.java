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

    KeywordDAO dao = new KeywordDAO();

    @Override
    public Optional<Keyword> getKeyword(int keywordId) {
        return dao.get(keywordId);
    }

    @Override
    public Keyword createKeyword(CreateKeywordArgs args) {
        return dao.addKeyword(args.getName(), Integer.parseInt(args.getTopicId()));
    }

    @Override
    public boolean deleteKeyword(int keywordId) {
        return dao.deleteKeyword(keywordId);
    }
}
