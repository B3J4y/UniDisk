package de.unidisk.crawler;


import de.unidisk.config.CrawlerConfiguration;
import de.unidisk.config.SolrConfiguration;
import de.unidisk.config.SystemConfiguration;
import de.unidisk.crawler.model.UniversitySeed;
import de.unidisk.crawler.simple.CrawlConfiguration;
import de.unidisk.crawler.simple.ICrawler;
import de.unidisk.crawler.simple.SimpleCrawl;
import de.unidisk.crawler.util.DomainHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class SimpleCrawlTest {

    PodamFactory podamFactory = new PodamFactoryImpl();

    /**
     * set to ignore because of travis
     * @throws Exception
     */
    @Test
    @Ignore
    public void shootTheMoon() throws Exception {
        final CrawlerConfiguration crawlerConfiguration = SystemConfiguration.getInstance().getCrawlerConfiguration();
        final UniversitySeed[] seeds = new UniversitySeed[]{
                new UniversitySeed("https://www.uni-potsdam.de/de/",0)
        };
        ICrawler crawler = new SimpleCrawl(
                crawlerConfiguration.getStorageLocation(),
                seeds,
                SolrConfiguration.getInstance().getCoreUrl(),
                CrawlConfiguration.fromCrawlerConfiguration(crawlerConfiguration)
        );
        crawler.startCrawl(seeds[0].getSeedUrl());
    }

    @Test
    public void getUrlDomain(){
        Assert.assertEquals(DomainHelper.getDomain("https://www.uni-tuebingen.de"), DomainHelper.getDomain("https://tobias-lib.uni-tuebingen.de/xmlui/bitstream/handle/10900/53589/Dissertation%20Boris%20M%c3%bcller.pdf?sequence=1&isAllowed=y"));
        Assert.assertEquals(DomainHelper.getDomain("https://www.tum.de"), DomainHelper.getDomain("\thttps://mediatum.ub.tum.de/download/1579844/1579844.pdf"));
    }
}
