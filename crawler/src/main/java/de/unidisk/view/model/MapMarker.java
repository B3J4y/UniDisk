package de.unidisk.view.model;

import de.unidisk.entities.hibernate.University;

public class MapMarker {

    String topicName;
    int topicId;
    University university;

    String iconUrl;

    public MapMarker(String topicName, int topicId, University university) {
        this.topicName = topicName;
        this.topicId = topicId;
        this.university = university;

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
        return university.getLat();
    }


    public double getLng() {
        return university.getLng();
    }


    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
