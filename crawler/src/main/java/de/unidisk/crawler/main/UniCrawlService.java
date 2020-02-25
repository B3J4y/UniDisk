package de.unidisk.crawler.main;

import de.unidisk.config.CrawlerConfig;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import de.unidisk.entities.hibernate.University;
import de.unidisk.repositories.HibernateUniversityRepo;

import java.util.List;
import java.util.stream.Collectors;

public class UniCrawlService {

    public UniCrawlService(IUniversityRepository projectRepository) {

        this.projectRepository = projectRepository;
    }

    private IUniversityRepository projectRepository;



    public void start(){

        final List<String> urls = projectRepository.getUniversities().stream().map(University::getSeedUrl).collect(Collectors.toList());

        String[] urlArr = new String[urls.size()];
        urlArr = urls.toArray(urlArr);
/*
        final SimpleCrawl crawler = new SimpleCrawl(
                CrawlerConfig.storageLocation,
                urlArr,
                urlArr,
                SolrStandardConfigurator.getStandardUrl(),
                100
        );
        crawler.startParallelCrawls();*/

    }

    public static void main(String[] args){
        final IUniversityRepository universityRepository = new HibernateUniversityRepo();

        UniCrawlService sapp = new UniCrawlService(universityRepository);
        sapp.start();
    }
}
