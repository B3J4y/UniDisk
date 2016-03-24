package uzuzjmd.competence.crawler.mysql;

import config.MagicStrings;
import mysql.MysqlConnect;
import mysql.VereinfachtesResultSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uzuzjmd.competence.crawler.exception.NoResultsException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 03.02.16.
 */
public class MysqlConnector {
    String connectionString = "jdbc:mysql://" + MagicStrings.thesaurusDatabaseUrl +
      "/%s" +
      "?user=" + MagicStrings.thesaurusLogin +
      "&password=" + MagicStrings.thesaurusPassword;
    private VereinfachtesResultSet hochschulen;
    private List<MysqlResult> mysqlResults;
    MysqlConnect connector;

    static private final Logger logger = LogManager.getLogger(MysqlConnector.class.getName());

    public MysqlConnector(String database) {
        connector = new MysqlConnect();
        String connection = String.format(connectionString, database);
        connector.connect(connection);
    }

    public void initHochschulen() throws NoResultsException {
        //logger.debug("Entering initHochschulen");
        hochschulen = connector.issueSelectStatement("Select * from " + MagicStrings.UNIVERITIESINITTABLE);
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
        connector.connect(connectionString);
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
        String query = "Select * from " + table + "_Stichwort";
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
        String query = "UPDATE `Overview` SET Status=" + String.valueOf(status) + " WHERE Name=\""
                + camp +"\"";
        connector.issueUpdateStatement(query);
        logger.debug("Leaving setCampaignStatus");

    }

    public boolean checkCampaignStatus (String camp) throws NoResultsException {
        logger.debug("Entering checkCampaignStatus with camp:" + camp);
        boolean res;
        String query = "SELECT Status from Overview where Name=\"" + camp + "\"";
        VereinfachtesResultSet result = connector.issueSelectStatement(query);
        if ((result == null) || (! result.isBeforeFirst()) ) {
            logger.debug("Leaving queryDomain with 0 fetches");
            throw new NoResultsException("No Results where fetched");
        }
        result.next();
        res = result.getInt("Status") == 1;
        logger.debug("Leaving checkCampaignStatus with " + String.valueOf(res));
        return res;
    }
}
