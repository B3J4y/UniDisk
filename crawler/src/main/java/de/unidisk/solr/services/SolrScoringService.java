package de.unidisk.solr.services;

import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.datatype.SolrStichwort;
import de.unidisk.crawler.model.CrawlDocument;
import de.unidisk.crawler.model.ScoreResult;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.University;
import de.unidisk.solr.SolrConnector;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SolrScoringService implements IScoringService {

    private IKeywordRepository keywordRepository;
    private ITopicRepository topicRepository;
    private SolrConfiguration solrConfiguration;

    public SolrScoringService(IKeywordRepository keywordRepository, ITopicRepository topicRepository, SolrConfiguration solrConfiguration) {
        this.keywordRepository = keywordRepository;
        this.solrConfiguration = solrConfiguration;
        this.topicRepository = topicRepository;
    }

    @Override
    public List<ScoreResult> getKeywordScore(int projectId, int keywordId) {
        final Optional<Keyword> keyword = this.keywordRepository.getKeyword(keywordId);
        if(!keyword.isPresent())
            return null;
        try {
            final SolrConnector solrConnector = new SolrConnector(solrConfiguration);
            final List<SolrDocument> docs = getKeyDocs(solrConnector, keyword.get().getName());
            final List<ScoreResult> results = docs.stream().map(d -> {
                final CrawlDocument crawlDocument = new CrawlDocument(d);
                final int universityId = crawlDocument.universityId;
                return new ScoreResult(
                        keywordId,
                        (float) d.get("score"),
                        universityId,
                        crawlDocument.datum,
                        crawlDocument.url
                );
            }).collect(Collectors.toList());
            return results;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public ScoreResult getTopicScore(int projectId, int topicId) {
        //TODO implement
        return new ScoreResult(
                topicId,
                1,
                0,
                System.currentTimeMillis(),
                ""
        );
    }

    private List<SolrDocument> getKeyDocs(SolrConnector connector, String key) throws IOException, SolrServerException {
        QueryResponse response = connector.query(new SolrStichwort(key).buildQuery(new ArrayList<>()));
        SolrDocumentList solrList = response.getResults();

        int sizeOfStichwortResult = Math.min((int) solrList.getNumFound(), SolrConfiguration.getInstance().getRowLimit());
        ArrayList<SolrDocument> documents = new ArrayList<>();
        for (int i = 0; i < sizeOfStichwortResult; i++) {
            SolrDocument doc = solrList.get(i);

            documents.add(doc);
        }
        return documents;
    }

}
