package uzuzjmd.competence.crawler.main;

import config.MagicStrings;
import neo4j.Neo4JConnector;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import uzuzjmd.competence.crawler.analysis.CrawlerDataAnalysis;
import uzuzjmd.competence.crawler.datatype.Model;
import uzuzjmd.competence.crawler.io.ReadCsv;
import uzuzjmd.competence.crawler.io.ReadMysql;
import uzuzjmd.competence.crawler.mysql.MysqlConnector;
import uzuzjmd.competence.crawler.solr.SolrConnector;

import java.util.Collection;

/**
 * Created by carl on 06.01.16.
 */
public class SolrApp {
    static private final Logger logger = LogManager.getLogger(SolrApp.class.getName());
    static private final String solrUrl = "http://learnlib.soft.cs.uni-potsdam.de:80/solr/basic";
    private String database;

    public SolrApp(String database) {
        this.database = database;
    }

    public  void excecute() throws Exception {
        DOMConfigurator.configure(MagicStrings.LOG4JLOCATION);
        MysqlConnector mc = new MysqlConnector(MagicStrings.UNIVERSITIESDBNAME);
        if (mc.checkCampaignStatus(this.database) == 1) {
            logger.warn("Campaign is already computing");
            return;
        }
        System.out.print("starting csv creation");
        mc.setCampaignStatus(this.database, 1);

        logger.debug(MagicStrings.ROOTPATH);
        logger.debug("Entering main");
        logger.info("Read out csv or mysql");
        try {
            //ReadCsv csv = new ReadCsv(MagicStrings.dataPath);
            ReadMysql mysql = new ReadMysql();
            Neo4JConnector nj = new Neo4JConnector();
            SolrConnector connector = new SolrConnector(solrUrl);

            CrawlerDataAnalysis cda = new CrawlerDataAnalysis(Integer.valueOf(MagicStrings.minPercentile),
                    Integer.valueOf(MagicStrings.maxPercentile), this.database);


            Model model = mysql.convertToModel(database);
            logger.info("New Model instance. Length - StichwortVar:" + model.stichwortVarSize() + " VarMeta:"
                    + model.varMetaSize());
            //model.insertSynonyms();
            logger.info("Model instance with Synonyms. Length - StichwortVar:" + model.stichwortVarSize() + " VarMeta:"
                    + model.varMetaSize());
            nj.queryMyStatements(model.toNeo4JQuery());
            logger.info("The model has been put into Neo4J");
            logger.info("Create Query");
            //model.scoreStichwort(connector);
            model.initStichFile(MagicStrings.stichWortPath);
            model.initVarMetaFile(MagicStrings.varMetaPath);
            model.scoreStichwort(connector, MagicStrings.stichWortPath);
            //model.scoreVariable(connector);
            model.scoreVariable(connector, MagicStrings.varMetaPath);
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
                if (values.length > Integer.valueOf(MagicStrings.maxPercentile)) {

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
        SolrApp sapp = new SolrApp("ForschendesLernen");
        sapp.excecute();
    }
}
