package de.unidisk.crawler.main;

import de.unidisk.config.CrawlerConfiguration;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.crawler.model.UniversitySeed;
import de.unidisk.crawler.simple.SimpleCrawl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class UniCrawlService {

    static private final Logger logger = LogManager.getLogger(UniCrawlService.class.getName());

    public UniCrawlService(IUniversityRepository projectRepository) {

        this.projectRepository = projectRepository;
    }

    private IUniversityRepository projectRepository;



    public void start(){
        logger.info("Start UniCrawlService");
        final List<UniversitySeed> seeds = projectRepository.getUniversities().stream().map(u -> new UniversitySeed(u.getSeedUrl(),u.getId())).collect(Collectors.toList());
        logger.info("Start Crawling " + seeds.size() + " websites.");
        UniversitySeed[] urlArr = new UniversitySeed[seeds.size()];
        urlArr = seeds.toArray(urlArr);
        final CrawlerConfiguration crawlerConfiguration = SystemConfiguration.getInstance().getCrawlerConfiguration();
        final SimpleCrawl crawler = new SimpleCrawl(
                crawlerConfiguration.getStorageLocation(),
                urlArr,
                SolrConfiguration.getInstance().getUrl(),
                crawlerConfiguration.getMaxVisits()
        );
        crawler.startParallelCrawls();
    }

}
