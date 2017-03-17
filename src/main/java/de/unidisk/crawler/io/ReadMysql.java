package de.unidisk.crawler.io;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import de.unidisk.common.mysql.VereinfachtesResultSet;
import de.unidisk.crawler.datatype.Model;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.mysql.MysqlConnector;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Created by carl on 02.03.16.
 */
public class ReadMysql {

    static private final Logger logger = LogManager.getLogger(ReadMysql.class.getName());

    public Model convertToModel(String database) throws NoResultsException, CommunicationsException {
        logger.debug("Entering contertToModel with database " + database);
        Model m = new Model(database);
        MysqlConnector mc = new MysqlConnector();
        VereinfachtesResultSet mr = mc.queryStichwortTable(database);
        while (mr.next()) {
            m.addDate(mr.getString("Stichwort"), mr.getString("Variable"), mr.getString("Metavariable"));
        }
        logger.debug("Leaving convertToModel");
        return m;
    }
}
