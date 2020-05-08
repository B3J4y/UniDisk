package de.unidisk.crawler;

import de.unidisk.config.CrawlerConfiguration;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.contracts.repositories.IUniversityRepository;
import de.unidisk.crawler.model.UniversitySeed;
import de.unidisk.crawler.simple.CrawlConfiguration;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.entities.hibernate.University;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class UniCrawlService implements SimpleCrawl.IProgressListener {

    static private final Logger logger = LogManager.getLogger(UniCrawlService.class.getName());

    public UniCrawlService(IUniversityRepository universityRepository) {

        this.universityRepository = universityRepository;
    }

    private IUniversityRepository universityRepository;



    public void start(){
        logger.info("Start UniCrawlService");
        final List<UniversitySeed> seeds = universityRepository.getUniversities().stream().map(u -> new UniversitySeed(u.getSeedUrl(),u.getId())).collect(Collectors.toList());
        if(seeds.size() == 0){
            logger.info("No seeds to crawl.");
            return;
        }
        start(seeds);
    }

    private void start(List<UniversitySeed> seeds){
        logger.info("Start Crawling " + seeds.size() + " websites.");
        UniversitySeed[] urlArr = new UniversitySeed[seeds.size()];
        urlArr = seeds.toArray(urlArr);
        final CrawlerConfiguration crawlerConfiguration = SystemConfiguration.getInstance().getCrawlerConfiguration();
        final CrawlConfiguration crawlConfiguration =   CrawlConfiguration.fromCrawlerConfiguration(crawlerConfiguration);

        final SimpleCrawl crawler = new SimpleCrawl(
                crawlerConfiguration.getStorageLocation(),
                urlArr,
                SolrConfiguration.getInstance().getUrl(),
                crawlConfiguration
        );
        crawler.setProgressListener(this);
        crawler.startParallelCrawls();
    }

    public void start(long timeSinceLastCrawl){
        logger.info("Start UniCrawlService");
        final List<University> unis = universityRepository.getUniversities(timeSinceLastCrawl);
        if(unis == null || unis.size() == 0){
            logger.info("No seeds to crawl.");
            return;
        }
        final List<UniversitySeed> seeds = unis.stream().map(u -> new UniversitySeed(u.getSeedUrl(),u.getId())).collect(Collectors.toList());
        start(seeds);
    }

    @Override
    public void onSeedFinished(UniversitySeed seed) {
        universityRepository.setLastCrawlTime(seed.getUniversityId(),System.currentTimeMillis());
    }
}
