package de.unidisk.crawler.datatype;

import de.unidisk.common.SystemProperties;
import de.unidisk.crawler.exception.NoDomainFoundException;
import de.unidisk.crawler.exception.NoHochschuleException;
import de.unidisk.crawler.exception.NoResultsException;
import de.unidisk.crawler.mysql.MysqlConnector;
import de.unidisk.crawler.mysql.MysqlResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by carl on 03.02.16.
 */
public class Urls {
    private List<UrlHochschule> urls;
    private Properties systemProperties = SystemProperties.getInstance();
    private MysqlConnector mysqlConn = new MysqlConnector(systemProperties.getProperty("uni.db.name"));
    public Urls() throws NoResultsException {
        urls = new ArrayList<>();
        mysqlConn.initHochschulen();

    }
    public void addDomain(String domain, String host) throws NoResultsException {
        for (UrlHochschule urlh :
                urls) {
            if (urlh.domain.equals(domain)) {
                urlh.count++;
                return;
            }
        }
        UrlHochschule urlh = new UrlHochschule();
        urlh.domain = domain;
        try {

            MysqlResult msr = mysqlConn.searchDomain(urlh.domain);
            urlh.hochschule = true;
            //vrs.next();
            urlh.Hochschulname = msr.hochschulname;
            urlh.lat = msr.lat;
            urlh.lon = msr.lon;
        } catch (NoResultsException e) { }
        urlh.count = 1;
        urls.add(urlh);
    }
    public boolean isHochschule(String domain) {
        for (UrlHochschule urlh :
                urls) {
            if (urlh.domain.equals(domain)) {
                return urlh.hochschule;
            }
        }
        return false;
    }
    public UrlHochschule getHochschule(String domain) throws NoDomainFoundException, NoHochschuleException {
        for (UrlHochschule urlh :
                urls) {
            if (urlh.domain.equals(domain)) {
                if (urlh.hochschule) {
                    return urlh;
                } else {
                    throw new NoHochschuleException("Domain " + domain + " is no Hochschule");
                }
            }
        }
        throw new NoDomainFoundException("Cannot find domain " + domain + " in urls");
    }
}
