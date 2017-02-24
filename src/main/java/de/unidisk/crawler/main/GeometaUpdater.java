package de.unidisk.crawler.main;

import de.unidisk.crawler.geomapper.Geocoordinator;

/**
 * Created by dehne on 03.02.2016.
 */
public class GeometaUpdater {
    public static void main (String[] args) {
        Geocoordinator.updateDBWithGeoData();
    }
}
