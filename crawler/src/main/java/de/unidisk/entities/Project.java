package de.unidisk.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    @OneToMany(fetch = FetchType.EAGER)
    private List<Topic> topics;

    public Project() {
    }

    public Project(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * TODO @Jan: implement state
     * @return
     */
    public ProjectState getStatus() {
        return ProjectState.RUNNING;
    }

    public void addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
        }
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }
}
