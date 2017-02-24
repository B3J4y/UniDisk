package de.unidisk.crawler.io;

import de.unidisk.common.SystemProperties;
import de.unidisk.common.mysql.VereinfachtesResultSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import de.unidisk.crawler.datatype.Model;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.mysql.MysqlConnector;

import java.util.Properties;

/**
 * Created by carl on 02.03.16.
 */
public class ReadMysql {

    static private final Logger logger = LogManager.getLogger(ReadMysql.class.getName());
    private Properties systemProperties = SystemProperties.getInstance();

    public Model convertToModel(String database) throws NoResultsException {
        logger.debug("Entering contertToModel with database " + database);
        Model m = new Model(database);
        MysqlConnector mc = new MysqlConnector(systemProperties.getProperty("uni.db.name"));
        VereinfachtesResultSet mr = mc.queryStichwortTable(database);
        while (mr.next()) {
            m.addDate(mr.getString("Stichwort"), mr.getString("Variable"), mr.getString("Metavariable"));
        }
        logger.debug("Leaving convertToModel");
        return m;
    }
}
