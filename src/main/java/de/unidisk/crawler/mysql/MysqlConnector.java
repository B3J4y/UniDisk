package de.unidisk.crawler.mysql;

import de.unidisk.common.SystemProperties;
import de.unidisk.common.mysql.MysqlConnect;
import de.unidisk.common.mysql.VereinfachtesResultSet;
import de.unidisk.crawler.exception.NoResultsException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by carl on 03.02.16.
 */
public class MysqlConnector {
    private Properties systemProperties = SystemProperties.getInstance();
    String connectionString = "jdbc:mysql://" + systemProperties.getProperty("database.localhost") +
      "/%s" +
      "?user=" + systemProperties.getProperty("database.root.name") +
      "&password=" + systemProperties.getProperty("database.root.password");
    private VereinfachtesResultSet hochschulen;
    private List<MysqlResult> mysqlResults;
    public MysqlConnect connector;

    static private final Logger logger = LogManager.getLogger(MysqlConnector.class.getName());

    public MysqlConnector(String database) {
        connector = new MysqlConnect();

        connector.connectToLocalhost();
    }


    public void initHochschulen() throws NoResultsException {
        //logger.debug("Entering initHochschulen");
        hochschulen = connector.issueSelectStatement("Select * from " + systemProperties.getProperty("uni.db.init"));
        if ((hochschulen == null) || (! hochschulen.isBeforeFirst()) ) {
            logger.debug("Leaving queryDomain with 0 fetches");
            throw new NoResultsException("No Results where fetched");
        }
        mysqlResults = new ArrayList<>();
        while (hochschulen.next() ) {
            MysqlResult msr = new MysqlResult();
            msr.bundesland = hochschulen.getString("Bundesland");
            msr.hochschulname = hochschulen.getString("Hochschulname");
            msr.hochschultyp = hochschulen.getString("Hochschultyp");
            msr.homepage = hochschulen.getString("Homepage");
            msr.lat = hochschulen.getDouble("lat");
            msr.lon = hochschulen.getDouble("lon");
            mysqlResults.add(msr);
        }

        //logger.debug("Leaving initHochschulen");
    }

    public void createNewCampaign(String campaign) {
        try {
            checkCampaignStatus(campaign);
            logger.debug("Created new Campaign " + campaign);
        } catch (NoResultsException e) {
            String query = "INSERT INTO `overview` (`Id`, `Name`, `Count`, `Status`) VALUES (NULL, '" + campaign + "', '0', '0')";
            connector.issueInsertOrDeleteStatement(query);
            logger.debug("Campaign " + campaign + " already exists.");
        }
    }

    public void deleteCampaing(String campaign) {
        try {
            checkCampaignStatus(campaign);
            String query = "DELETE FROM `overview` WHERE `overview`.`Name` = \"" + campaign + "\" ";
            connector.issueInsertOrDeleteStatement(query);
            logger.debug("Deleted campaign " + campaign);
        } catch (NoResultsException e) {
            logger.debug("Campaign " + campaign + " doesn't exist.");
        }
    }

    public MysqlResult searchDomain(String domain) throws NoResultsException {
        //logger.debug("Entering searchDomain with domain:" + domain);
        for (MysqlResult msr :
                mysqlResults) {
            if (msr.homepage.contains(domain)) {
                //logger.debug("Leaving queryDomain with fetches");
                return msr;
            }
        }
        //logger.debug("Leaving queryDomain with 0 fetches");
        throw new NoResultsException("No Domain found");
    }
    public VereinfachtesResultSet queryDomain(String domain) throws NoResultsException {
        //logger.debug("Entering queryDomain with domain:" + domain);
        MysqlConnect connector = new MysqlConnect();
        connector.connectToLocalhost();
        String query = "Select * from Hochschulen where Homepage like ?";
        VereinfachtesResultSet result = connector.issueSelectStatement(query, "%" + domain + "%");
        if ((result == null) || (! result.isBeforeFirst()) ) {
            //logger.debug("Leaving queryDomain with 0 fetches");
            throw new NoResultsException("No Results where fetched");
        }
        //logger.debug("Leaving queryDomain with >0 fetches");
        return result;
    }

    public VereinfachtesResultSet queryStichwortTable(String table) throws NoResultsException {
        logger.debug("Entering queryDomain with domain:" + table);
        String query = "Select * from " + table + "_" + systemProperties.getProperty("suffix.stickWort");
        VereinfachtesResultSet result = connector.issueSelectStatement(query );
        if ((result == null) || (! result.isBeforeFirst()) ) {
            logger.debug("Leaving queryDomain with 0 fetches");
            throw new NoResultsException("No Results where fetched");
        }
        logger.debug("Leaving queryDomain with >0 fetches");
        return result;

    }

    //Status: 0 = nothing done, 1 = work in progress, 2 = work done successfully, 3 = work aborted, because of reasons
    public void setCampaignStatus(String camp, int status) {
        logger.debug("Entering setCampaignStatus with camp:" + camp + ", status:" + String.valueOf(status));
        String query = "UPDATE overview SET Status=" + String.valueOf(status) + " WHERE Name=\""
                + camp +"\"";
        connector.issueUpdateStatement(query);
        logger.debug("Leaving setCampaignStatus");

    }

    public int checkCampaignStatus (String camp) throws NoResultsException {
        logger.debug("Entering checkCampaignStatus with camp:" + camp);
        int res;
        String query = "SELECT Status from overview where Name=\"" + camp + "\"";
        VereinfachtesResultSet result = connector.issueSelectStatement(query);
        if ((result == null) || (! result.isBeforeFirst()) ) {
            logger.debug("Leaving queryDomain with 0 fetches");
            throw new NoResultsException("No Results where fetched");
        }
        result.next();
        res = result.getInt("Status");
        logger.debug("Leaving checkCampaignStatus with " + String.valueOf(res));
        return res;
    }
}
