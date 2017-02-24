package de.unidisk.crawler.geomapper;


import de.unidisk.common.mysql.MysqlConnect;
import de.unidisk.common.mysql.VereinfachtesResultSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dehne on 03.02.2016.
 */
public class Geocoordinator {

    private static final String connextionString = MysqlConnect.getLocalhostConnection();

    public static void updateDBWithGeoData() {
        List<HochschulResultSet> hochschulResultSetList = getHochResultSetFromDB();
        updateSet(hochschulResultSetList);
        updateDatabase(hochschulResultSetList);
    }

    private static void updateDatabase(List<HochschulResultSet> hochschulResultSetList) {
        MysqlConnect conn = new MysqlConnect();
        conn.connect(connextionString);
        conn.issueUpdateStatement("Use hochschulen;");
        /*try {
            conn.issueUpdateStatement("ALTER TABLE hochschulen_copy ADD lat double");
            conn.issueUpdateStatement("ALTER TABLE hochschulen_copy ADD lon double");
        } catch( Exception e) {

        }*/
        for (HochschulResultSet hochschulResultSet : hochschulResultSetList) {
            conn.issueUpdateStatement("UPDATE hochschulen_copy SET lon = ? WHERE `HS-Nr` = ?", hochschulResultSet.getLon(), hochschulResultSet.getId());
            conn.issueUpdateStatement("UPDATE hochschulen_copy SET lat = ? WHERE `HS-Nr` = ?", hochschulResultSet.getLat(), hochschulResultSet.getId());
        }
        conn.close();
    }

    private static List<HochschulResultSet> getHochResultSetFromDB() {
        MysqlConnect mysqlConnect = new MysqlConnect();
        mysqlConnect.connect(connextionString);
        mysqlConnect.issueUpdateStatement("Use hochschulen;");
        VereinfachtesResultSet mysqlResult = mysqlConnect.issueSelectStatement("SELECT `HS-Nr`,`Straße`,`Ort` FROM `hochschulen_copy`");
        java.util.List<HochschulResultSet> hochschulResultSetList = new LinkedList<>();
        while (!mysqlResult.isLast()) {
            mysqlResult.next();
            HochschulResultSet elem = new HochschulResultSet(mysqlResult.getInt("HS-Nr"), mysqlResult.getString("Straße"), mysqlResult.getString("Ort"));
            hochschulResultSetList.add(elem);
        }
        mysqlConnect.close();
        return hochschulResultSetList;
    }

    private static List<HochschulResultSet> updateSet(List<HochschulResultSet> input) {
        for (HochschulResultSet hochschulResultSet : input) {
            updateSingleSet(hochschulResultSet);
        }
        return input;
    }

    private static void updateSingleSet(HochschulResultSet input) {
        Client client2 = ClientBuilder.newClient();
        input.setCity(input.getCity().replaceAll(" ", "+"));
        input.setStreet(input.getStreet().replaceAll(" ", "+"));
        WebTarget target2 = client2.target("http://nominatim.openstreetmap.org/search?q=" + input.getStreet() + "," + input.getCity() + "&format=json&polygon=1&addressdetails=1");
        Response response = target2.request(
                MediaType.APPLICATION_JSON).get();

        ArrayList<HashMap<String, String>> result = response.readEntity(ArrayList.class);
        if (result == null || result.isEmpty()) {
            return;
        }
        try {
            Double lat = Double.valueOf(result.get(0).get("lat"));
            Double lon = Double.valueOf(result.get(0).get("lon"));
            input.setLat(lat);
            input.setLon(lon);
        } catch (NumberFormatException e) {
            Class<Geocoordinator> geocoordinatorClass2 = Geocoordinator.class;
            System.exit(-1);
        }
        client2.close();
    }
}
