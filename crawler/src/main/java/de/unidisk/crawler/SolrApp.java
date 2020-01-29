package de.unidisk.crawler;

import de.unidisk.common.SystemProperties;
import de.unidisk.crawler.analysis.CrawlerDataAnalysis;
import de.unidisk.crawler.datatype.Model;
import de.unidisk.crawler.io.ReadMysql;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.crawler.solr.SolrConnector;
import de.unidisk.crawler.solr.SolrStandardConfigurator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Properties;

/**
 * Created by carl on 06.01.16.
 */
public class SolrApp {
    static private final Logger logger = LogManager.getLogger(SolrApp.class.getName());
    private static Properties systemProperties = SystemProperties.getInstance();
    private String database;

    public SolrApp(String database) {
        this.database = database;
    }

    public  void execute() throws Exception {
        MysqlConnector mc = new MysqlConnector();
        if (mc.checkCampaignStatus(this.database) == 1) {
            logger.warn("Campaign is already computing");
            return;
        }
        System.out.print("starting csv creation");
        mc.setCampaignStatus(this.database, 1);

        logger.debug("Entering main");
        logger.info("Read out csv or mysql");
        try {
            //ReadCsv csv = new ReadCsv(MagicStrings.dataPath);
            ReadMysql mysql = new ReadMysql();
            SolrConnector connector = new SolrConnector(SolrStandardConfigurator.getStandardUrl(),
                    systemProperties.getProperty("solr.connection.db"));

            CrawlerDataAnalysis cda = new CrawlerDataAnalysis(
                    Integer.valueOf(systemProperties.getProperty("values.percentile.min")),
                    Integer.valueOf(systemProperties.getProperty("values.percentile.max")), this.database);


            Model model = mysql.convertToModel(database);
            logger.info("New Model instance. Length - StichwortVar:" + model.stichwortVarSize() + " VarMeta:"
                    + model.varMetaSize());
            //model.insertSynonyms();
            logger.info("Model instance with Synonyms. Length - StichwortVar:" + model.stichwortVarSize() + " VarMeta:"
                    + model.varMetaSize());
            logger.info("The model has been put into Neo4J");
            logger.info("Create Query");
            model.initStichFile("NOPATH");
            model.initVarMetaFile("NOPATH");
            model.scoreStichwort(connector, "NOPATH");
            model.scoreVariable(connector, "NOPATH");
            logger.info("Get Score from Crawling");
            logger.info("Transform Scoring into results");
            //model.stichwortVarToCsv(MagicStrings.stichWortVarPath);
            //model.stichwortResultToCsv(MagicStrings.stichWortPath);
            //model.varMetaResultToCsv(MagicStrings.varMetaPath);

            for (String var : model.getVarMeta().getVariables()) {
                logger.debug("Search percentile for " + var);
                cda.prepareHochschuleSolrAnalyse(var);

                double[] values = ArrayUtils.toPrimitive(cda.inputData.keySet().toArray(new Double[0]));

                logger.debug("There are " + values.length + " elements in the table");
                if (values.length > Integer.valueOf(systemProperties.getProperty("values.percentile.max"))) {
                    Collection<String> relevantData = cda.selectRelevantDataForPlotting();
                    cda.deleteInDatabase(relevantData, var);
                } else {
                    logger.debug("The results in the table don't exceed the maximum limit.");
                }
            }
            mc.setCampaignStatus(this.database, 2);
            logger.debug("Leaving main");
        } catch (Exception e) {
            logger.error(e);

            mc.setCampaignStatus(this.database, 3);
            e.getStackTrace();

        }

    }
    public static void main(String[] args) throws Exception {
        SolrApp sapp = new SolrApp("up_test");
        sapp.execute();
    }
}
