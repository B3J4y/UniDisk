package de.unidisk.solr.services;

import de.unidisk.common.exceptions.EntityNotFoundException;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.contracts.repositories.IKeywordRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.contracts.services.IScoringService;
import de.unidisk.crawler.datatype.SolrStichwort;
import de.unidisk.crawler.model.CrawlDocument;
import de.unidisk.crawler.model.ScoreResult;
import de.unidisk.crawler.util.DomainHelper;
import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.TopicScore;
import de.unidisk.entities.hibernate.University;
import de.unidisk.solr.SolrConnector;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SolrScoringService implements IScoringService {

    private IKeywordRepository keywordRepository;
    private ITopicRepository topicRepository;
    private SolrConfiguration solrConfiguration;
    private IUniversityRepository universityRepository;

    private Map<String,Integer> universityDomainMap;

    public SolrScoringService(IKeywordRepository keywordRepository, ITopicRepository topicRepository, SolrConfiguration solrConfiguration, IUniversityRepository universityRepository) {
        this.keywordRepository = keywordRepository;
        this.solrConfiguration = solrConfiguration;
        this.topicRepository = topicRepository;
        this.universityRepository = universityRepository;
    }

    private void setupUniversityDomains(){
        if(universityDomainMap != null)
            return;
        universityDomainMap = this.buildUniversityMap();
    }

    private Map<String,Integer> buildUniversityMap(){
        final List<University> universities = universityRepository.getUniversities();
        final Map<String,Integer> universityMap = new HashMap<>();
        for(University university : universities){
            final String domain = DomainHelper.getDomain(university.getSeedUrl());
            universityMap.put(domain,university.getId());
        }
        return universityMap;
    }

    @Override
    public List<ScoreResult> getKeywordScore(int projectId, int keywordId) {
        final Optional<Keyword> keyword = this.keywordRepository.getKeyword(keywordId);
        if(!keyword.isPresent())
            return null;

        setupUniversityDomains();
        try {
            final SolrConnector solrConnector = new SolrConnector(solrConfiguration);
            final List<SolrDocument> docs = getKeyDocs(solrConnector, keyword.get().getName());
            final List<ScoreResult> results = docs.stream().map(d -> {
                final CrawlDocument crawlDocument = new CrawlDocument(d);
                final String resultDomain = DomainHelper.getDomain(crawlDocument.url);

                if(!universityDomainMap.containsKey(resultDomain)){
                    System.out.println("Unable to find university for domain: " + resultDomain);
                }
                final int universityId = universityDomainMap.get(resultDomain);
                return new ScoreResult(
                        keywordId,
                        (float) d.get("score"),
                        universityId,
                        crawlDocument.datum,
                        crawlDocument.url,
                        crawlDocument.title
                );
            }).collect(Collectors.toList());
            return results;
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<ScoreResult> getTopicScores(int projectId, int topicId) throws EntityNotFoundException {
        final List<TopicScore> scores = topicRepository.getScores(topicId);
        final List<ScoreResult> scoreResults = scores.stream().map(s ->
             new ScoreResult(
                    s.getTopic().getId(),
                    s.getScore(),
                    s.getSearchMetaData().getUniversity().getId(),
                    0,
                    s.getSearchMetaData().getUrl(),
                     null
            )
        ).collect(Collectors.toList());
        return scoreResults;
    }

    @Override
    public boolean canEvaluate() {
        SolrQuery q = new SolrQuery("*:*");
        q.setRows(0);  // don't request data
        try {
            long coreDocuments =  new SolrConnector(solrConfiguration).query(q).getResults().getNumFound();
            return coreDocuments > 50;
        } catch (IOException | SolrServerException e) {
            // Not being able to connect to Solr automatically means that we can't evaluate
            e.printStackTrace();
        }
        return false;
    }

    private List<SolrDocument> getKeyDocs(SolrConnector connector, String key) throws IOException, SolrServerException {
        final SolrQuery query =new SolrStichwort(key).buildQuery(new ArrayList<>());
        QueryResponse response = connector.query(query);
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
