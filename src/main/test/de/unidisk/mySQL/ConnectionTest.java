package de.unidisk.mySQL;

import de.unidisk.common.mysql.MysqlConnect;
import de.unidisk.crawler.mysql.MysqlConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by user on 17.03.2017.
 */
public class ConnectionTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testNoDatabase() throws Exception {
        MysqlConnect mySqlConnection = MysqlConnector.getInstance();
        Assert.assertNotNull("Table 'overview' not found", mySqlConnection.issueSelectStatement("SELECT * FROM overview;"));
    }

    @After
    public void tearDown() throws Exception {
        MysqlConnect mySqlConnection = MysqlConnector.getInstance();
        mySqlConnection.otherStatements("DROP DATABASE `unidisk`");

    }
}