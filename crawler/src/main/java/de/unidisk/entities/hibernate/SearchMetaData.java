package de.unidisk.entities.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class SearchMetaData {
    @Id
    @GeneratedValue
    private int id;
    private String url;
    
    @OneToOne
    private University university;
    private Long timestamp;

    public SearchMetaData(){

    }


    public SearchMetaData(String url, University university, Long timestamp) {
        this.url = url;
        this.university = university;
        this.timestamp = timestamp;
    }

    public SearchMetaData(String url, Long timestamp) {
        this.url = url;

        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
