package uzuzjmd.competence.crawler.datatype;

/**
 * Created by carl on 03.02.16.
 */
public class UrlHochschule {
    String domain;
    String Hochschulname;
    boolean hochschule;
    int count;
    boolean queried;
    double lat;
    double lon;

    public UrlHochschule() {
        domain = "";
        Hochschulname = "";
        hochschule = false;
        count = 1;
        queried = false;
        lat = -1;
        lon = -1;
    }
}
