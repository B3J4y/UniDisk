package de.unidisk.view.model;

import de.unidisk.entities.hibernate.University;

public class MapMarker {

    String topicName;
    int topicId;
    University university;
    double lat, lng;
    String iconUrl;

    public MapMarker(String topicName, int topicId, University university, double lat, double lng) {
        this.topicName = topicName;
        this.topicId = topicId;
        this.university = university;
        this.lat = lat;
        this.lng = lng;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
