package uzuzjmd.competence.crawler.io;

import mysql.VereinfachtesResultSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uzuzjmd.competence.crawler.datatype.Model;
import uzuzjmd.competence.crawler.exception.NoResultsException;
import uzuzjmd.competence.crawler.mysql.MysqlConnector;

/**
 * Created by carl on 02.03.16.
 */
public class ReadMysql {

    static private final Logger logger = LogManager.getLogger(ReadMysql.class.getName());

    public Model convertToModel(String database) throws NoResultsException {
        logger.debug("Entering contertToModel with database " + database);
        Model m = new Model(database);
        MysqlConnector mc = new MysqlConnector("unidisk");
        VereinfachtesResultSet mr = mc.queryStichwortTable(database);
        while (mr.next()) {
            m.addDate(mr.getString("Stichwort"), mr.getString("Variable"), mr.getString("Metavariable"));
        }
        logger.debug("Leaving convertToModel");
        return m;
    }
}
