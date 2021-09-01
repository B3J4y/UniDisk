package de.unidisk.entities.hibernate;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class University {
    @Id
    @GeneratedValue
    private int id;
    private String name;

    private double lat;
    private double lng;
    private String seedUrl;

    //in milliseconds
    private long lastCrawl;

    @ColumnDefault("false")
    private boolean crawlingDisabled;


    public University() {
    }

    public University(int id, String name, double lat, double lng, String seedUrl, long lastCrawl, boolean crawlingDisabled) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.seedUrl = seedUrl;
        this.lastCrawl = lastCrawl;
        this.crawlingDisabled = crawlingDisabled;
    }

    public University(String name) {
        this.name = name;
    }

    public University(String name, double lat, double lng, String seedUrl) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.seedUrl = seedUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public long getLastCrawl() {
        return lastCrawl;
    }

    public void setLastCrawl(long lastCrawl) {
        this.lastCrawl = lastCrawl;
    }

    public boolean isCrawlingDisabled() {
        return crawlingDisabled;
    }

    public void setCrawlingDisabled(boolean crawlingDisabled) {
        this.crawlingDisabled = crawlingDisabled;
    }
}
