package uzuzjmd.competence.crawler.analysis;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import config.MagicStrings;
import datastructures.Pair;
import mysql.VereinfachtesResultSet;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import uzuzjmd.competence.crawler.mysql.MysqlConnector;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by dehne on 07.04.2016.
 */
public class CrawlerDataAnalysis {

    private Integer minResults = 10;
    private Integer maxResults = 25;
    private String tableName = "test";
    public HashMap<Double, String> inputData;
    private MysqlConnector mysqlConnect = new MysqlConnector(MagicStrings.UNIVERSITIESDBNAME);

    static Logger logger = Logger.getLogger(CrawlerDataAnalysis.class);

    public CrawlerDataAnalysis() {

    }

    public CrawlerDataAnalysis(int minResults, int maxResults, String tableName) {
        this.minResults = minResults;
        this.maxResults = maxResults;
        this.tableName = tableName;
    }

    public void prepareHochschuleSolrAnalyse() {
        logger.debug("Entering prepareHochschuleSolrAnalyse");
        LinkedList<Pair<Double>> latLonsTaken = new LinkedList<Pair<Double>>();
        VereinfachtesResultSet result = mysqlConnect.connector.issueSelectStatement("Select Hochschule, SolrScore, Lat, Lon from "
                + tableName + "_"
                + MagicStrings.varMetaSuffix + " Where Lat != -1");
        inputData = new HashMap<>();
        HashMap<Pair<Double>, Double> latLongSolrMap = new HashMap<>();
        if (! result.isBeforeFirst()) {
            logger.warn(tableName + "_" + MagicStrings.varMetaSuffix + " Database is empty");
            return;
        }
        while(result.next()) {
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
        while (notInRange) {
            final double percentile = descriptiveStatistics.getPercentile(guess);
            logger.trace("percentile for guess " + guess + " is: " + percentile);
            final Set<Double> filteredSet = Sets.filter(inputData.keySet(), new Predicate<Double>() {
                @Override
                public boolean apply(@Nullable Double input) {
                    return input > percentile;
                }
            });
            logger.info("number of values over percentile are: " + filteredSet.size());
            guess = guess - 1.0;
            if (guess == 0) {
                break;
            } else {
                if (filteredSet.size() > minResults && filteredSet.size() < maxResults) {
                    Collection<String> finalResult = new LinkedHashSet<>();
                    for (Double aDouble : filteredSet) {
                        finalResult.add(inputData.get(aDouble));
                    }
                    return finalResult;
                }
            }
        }
        // TODO: Exception
        return null;
    }

    public void deleteInDatabase(Collection<String> inputData) {
        String str = StringUtils.join(inputData.toArray(), "\", \"");
        mysqlConnect.connector.issueInsertOrDeleteStatement("DELETE FROM `UnitTest_varMeta` WHERE NOT (Hochschule) IN (\"" + str + "\")");
    }
}
