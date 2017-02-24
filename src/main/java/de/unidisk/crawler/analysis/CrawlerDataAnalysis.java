package de.unidisk.crawler.analysis;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import de.unidisk.common.SystemProperties;
import de.unidisk.common.mysql.VereinfachtesResultSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.common.datastructures.Pair;


import java.util.*;

/**
 * Created by dehne on 07.04.2016.
 */
public class CrawlerDataAnalysis {

    private Integer minResults = 10;
    private Integer maxResults = 25;
    private String tableName = "test";
    public HashMap<Double, String> inputData;
    private Properties systemProperties = SystemProperties.getInstance();

    private MysqlConnector mysqlConnect = new MysqlConnector(systemProperties.getProperty("uni.db.name"));

    static Logger logger = Logger.getLogger(CrawlerDataAnalysis.class);

    public CrawlerDataAnalysis() {

    }

    public CrawlerDataAnalysis(int minResults, int maxResults, String tableName) {
        this.minResults = minResults;
        this.maxResults = maxResults;
        this.tableName = tableName;
    }

    public void prepareHochschuleSolrAnalyse(String var) {
        logger.debug("Entering prepareHochschuleSolrAnalyse");
        LinkedList<Pair<Double>> latLonsTaken = new LinkedList<Pair<Double>>();
        String query = "Select Hochschule, SolrScore, Lat, Lon from "
                + tableName + "_"
                + systemProperties.getProperty("suffix.varMeta") + " Where Lat != -1 "
                + "AND Variable=\"" + var + "\"";
        VereinfachtesResultSet result = mysqlConnect.connector.issueSelectStatement(query);
        inputData = new HashMap<>();
        HashMap<Pair<Double>, Double> latLongSolrMap = new HashMap<>();
        if (!result.isBeforeFirst()) {
            logger.warn(tableName + "_" + systemProperties.getProperty("suffix.varMeta") + " Database is empty");
            return;
        }
        while (result.next()) {
            Pair<Double> latLonPair = new Pair<Double>(result.getDouble("Lat"), result.getDouble("Lon"));
            Double solrScore = result.getDouble("SolrScore");
            String hochschule = result.getString("Hochschule");
            if (latLonsTaken.contains(latLonPair)) {
                Double oldValue = latLongSolrMap.get(latLonPair);
                solrScore = oldValue + solrScore;
                inputData.remove(oldValue);
            }
            latLonsTaken.add(latLonPair);
            inputData.put(solrScore, hochschule);
            latLongSolrMap.put(latLonPair, solrScore);
        }
        if (inputData.isEmpty()) {
            logger.error("Couldn't find any data to utilize");
            // TODO: 11.04.16 Build an exception
        }
        logger.debug("Leaving prepareHochschuleSolrAnalyse");
    }


    public Collection<String> selectRelevantDataForPlotting() {
        if (inputData.isEmpty()) {
            logger.error("Couldn't find any data to utilize");
            // TODO: 11.04.16 Build an exception
            return null;
        }
        double[] values = ArrayUtils.toPrimitive(inputData.keySet().toArray(new Double[0]));


        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(values);
        double guess = 100; // init value
        Boolean notInRange = true;
        int sizeOfPerc = 0;
        while (notInRange) {
            final double percentile = descriptiveStatistics.getPercentile(guess);
            logger.trace("percentile for guess " + guess + " is: " + percentile);
            final Set<Double> filteredSet = Sets.filter(inputData.keySet(), new Predicate<Double>() {
                @Override
                public boolean apply(Double input) {
                    return input > percentile;
                }
            });
            sizeOfPerc = filteredSet.size();

            guess = guess - 1.0;
            if (guess == 0) {
                break;
            } else {
                if (filteredSet.size() > minResults && filteredSet.size() < maxResults) {
                    Collection<String> finalResult = new LinkedHashSet<>();
                    for (Double aDouble : filteredSet) {
                        finalResult.add(inputData.get(aDouble));
                    }
                    logger.info("number of values over percentile are: " + sizeOfPerc);
                    return finalResult;
                }
            }
        }

        // TODO: Exception
        return null;
    }

    public void deleteInDatabase(Collection<String> inputData, String var) {

        String str = StringUtils.join(inputData.toArray(), "\", \"");
        String query = "DELETE FROM `" + tableName + "_"
                + systemProperties.getProperty("suffix.varMeta") + "` WHERE NOT (Hochschule) IN (\"" + str + "\") "
                + "AND Variable=\"" + var + "\"";
        mysqlConnect.connector.issueInsertOrDeleteStatement(query);
    }
}
