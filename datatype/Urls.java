package uzuzjmd.competence.crawler.datatype;

import config.MagicStrings;
import uzuzjmd.competence.crawler.exception.NoDomainFoundException;
import uzuzjmd.competence.crawler.exception.NoHochschuleException;
import uzuzjmd.competence.crawler.exception.NoResultsException;
import uzuzjmd.competence.crawler.mysql.MysqlConnector;
import uzuzjmd.competence.crawler.mysql.MysqlResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl on 03.02.16.
 */
public class Urls {
    private List<UrlHochschule> urls;
    private MysqlConnector mysqlConn = new MysqlConnector(MagicStrings.UNIVERSITIESDBNAME);
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
