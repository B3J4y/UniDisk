package uzuzjmd.competence.crawler.builder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by carl on 06.01.16.
 */
public class QueryBuilder {
    static private final Logger logger = LogManager.getLogger(QueryBuilder.class.getName());
    public String buildQueryForVar(String var) {
        logger.debug("Entering buildQueryForVar with var:" + var);
        String q = "";

        logger.debug("Leaving buildQueryForVar with Query:" + q);
        return q;
    }

}
