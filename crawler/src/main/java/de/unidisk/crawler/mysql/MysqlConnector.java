package de.unidisk.crawler.mysql;

import de.unidisk.common.SystemProperties;
import de.unidisk.common.mysql.MysqlConnect;
import de.unidisk.common.mysql.VereinfachtesResultSet;
import de.unidisk.crawler.exception.NoResultsException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by carl on 03.02.16.
 */
public class MysqlConnector extends MysqlConnect {
    private Properties systemProperties = SystemProperties.getInstance();
    private VereinfachtesResultSet hochschulen;
    private List<MysqlResult> mysqlResults;

    static private final Logger logger = LogManager.getLogger(MysqlConnector.class.getName());
    private static MysqlConnector connectionUnidisk;

    public MysqlConnector() {
        super();
        connectToLocalhost();
    }

    public static MysqlConnector getInstance() {
        if(connectionUnidisk == null) {
            connectionUnidisk = new MysqlConnector();
        }
        return connectionUnidisk;
    }


    public void initHochschulen() throws NoResultsException {
        //logger.debug("Entering initHochschulen");
        hochschulen = issueSelectStatement("Select * from " + systemProperties.getProperty("uni.db.init"));
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
            getInstance().issueInsertOrDeleteStatement(query);
            logger.debug("Campaign " + campaign + " already exists.");
        }
    }

    public void deleteCampaing(String campaign) {
        try {
            checkCampaignStatus(campaign);
            String query = "DELETE FROM `overview` WHERE `overview`.`Name` = \"" + campaign + "\" ";
            connectionUnidisk.issueInsertOrDeleteStatement(query);
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

    public VereinfachtesResultSet queryStichwortTable(String table) throws NoResultsException {
        logger.debug("Entering queryDomain with domain:" + table);
        String query = "Select * from " + table + "_" + systemProperties.getProperty("suffix.stickWort");
        VereinfachtesResultSet result = issueSelectStatement(query );
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
        issueUpdateStatement(query);
        logger.debug("Leaving setCampaignStatus");

    }

    public int checkCampaignStatus (String camp) throws NoResultsException {
        logger.debug("Entering checkCampaignStatus with camp:" + camp);
        int res;
        String query = "SELECT Status from overview where Name=\"" + camp + "\"";
        VereinfachtesResultSet result = issueSelectStatement(query);
        if ((result == null) || (! result.isBeforeFirst()) ) {
            logger.debug("Leaving queryDomain with 0 fetches");
            throw new NoResultsException("No Results where fetched");
        }
        result.next();
        res = result.getInt("Status");
        logger.debug("Leaving checkCampaignStatus with " + String.valueOf(res));
        return res;
    }

    @Override
    protected void createSchema(String name) {
        logger.debug("Entering createSchemaWithTables - "+name);
        //utf8mb4 requires MySQL 5.5.3 (released in early 2010)
        String query = "CREATE DATABASE `"+name+"` DEFAULT CHARACTER SET utf8mb4 ;";
        otherStatements(query);
        logger.debug("Leaving createSchemaWithTables -"+name);
    }

    @Override
    protected void readDump(String dbName, String fileName) {
        logger.debug("Entering readDump DB: "+dbName+" File: "+fileName);

        String[] path = {".", "src", "main", "resources", "sql_dumps", fileName};
        String filePath = String.join(File.separator, path);

        File file = new File(filePath);
        if(file.exists() && !file.isDirectory()) {
            Runtime rt = Runtime.getRuntime();
            try {
                String ex = "mysql --user=" + systemProperties.getProperty("database.root.name") + " --password=" + systemProperties.getProperty("database.root.password")+ " " + systemProperties.getProperty("uni.db.name") + " source \"" + file.getAbsolutePath() + "\" ";
                Process pr = rt.exec(ex);
                logger.debug("MSQLServer import file exit value: " + String.valueOf(pr.exitValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        logger.debug("Leaving readDump DB: "+dbName+" File: "+fileName);
    }
}
