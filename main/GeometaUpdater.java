package uzuzjmd.competence.crawler.main;

import uzuzjmd.competence.crawler.geomapper.Geocoordinator;

/**
 * Created by dehne on 03.02.2016.
 */
public class GeometaUpdater {
    public static void main (String[] args) {
        Geocoordinator.updateDBWithGeoData();
    }
}
