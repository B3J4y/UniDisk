package de.unidisk.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by carl on 29.01.17.
 */
public class SystemConfiguration {
  private static SystemConfiguration systemConfiguration;
  private String versionedFile = "unidisk.properties";
  private CrawlerConfiguration crawlerConfiguration;
  private SolrConfiguration solrConfiguration;
  private DatabaseConfiguration databaseConfiguration;

  private boolean production;
  private long scoringInterval;

  protected SystemConfiguration() throws IOException {
    Properties properties = new Properties();
    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(versionedFile);
    properties.load(resourceAsStream);

    crawlerConfiguration = crawlConfigurationFromProperties(properties);
    solrConfiguration = solrConfigurationFromProperties(properties);
    databaseConfiguration = databaseConfigurationFromProperties(properties);
    production = properties.getProperty("environment.production").equals("1");
    scoringInterval = Long.parseLong(properties.getProperty("scoring.interval"));
  }

  private static SolrConfiguration solrConfigurationFromProperties(Properties properties){
    final String prefix = "solr.";

    final String server = properties.getProperty(prefix+"connection.url");
    final int port = Integer.parseInt(properties.getProperty(prefix+"connection.port"));
    final String core = properties.getProperty(prefix+"core");
    final int rowLimit = Integer.parseInt(properties.getProperty(prefix+"rowLimit"));
    return new SolrConfiguration(
            server,
            port,
            rowLimit,
            core
    );
  }

  private static CrawlerConfiguration crawlConfigurationFromProperties(Properties properties){
    final String prefix = "crawler.";


    final String storageLocation = properties.getProperty(prefix+"storageLocation");
    final int maxDepth = Integer.parseInt(properties.getProperty(prefix+"maxDepth"));
    final int maxVisits = Integer.parseInt(properties.getProperty(prefix+"maxSeedVisits"));
    final long uniCrawlInterval = Long.parseLong(properties.getProperty(prefix+"uni.interval"));
    final long crawlInterval = Long.parseLong(properties.getProperty(prefix+"interval"));
    return new CrawlerConfiguration(
            storageLocation,
            maxDepth,
            maxVisits,
            uniCrawlInterval,
            crawlInterval
    );
  }

  private static DatabaseConfiguration databaseConfigurationFromProperties(Properties properties){
    final String prefix = "database.";


    final boolean mockData = properties.getProperty(prefix+"initializeMockData").equals("1");
    final String config = properties.getProperty(prefix+"config");
    return new DatabaseConfiguration(
            mockData,
            config
    );
  }

  public static SystemConfiguration getInstance() {
    if (systemConfiguration == null) {
      try {
        systemConfiguration = new SystemConfiguration();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return systemConfiguration;
  }

  public CrawlerConfiguration getCrawlerConfiguration() {
    return crawlerConfiguration;
  }

  public SolrConfiguration getSolrConfiguration() {
    return solrConfiguration;
  }

  public DatabaseConfiguration getDatabaseConfiguration() {
    return databaseConfiguration;
  }

  public boolean isProduction() {
    return production;
  }

  public long getScoringInterval() {
    return scoringInterval;
  }
}
