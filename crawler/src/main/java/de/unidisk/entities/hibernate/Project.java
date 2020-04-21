package de.unidisk.entities.hibernate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    @Id

    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProjectState projectState;

    @Column(nullable = true)
    private String processingError;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Topic> topics;

    public Project() {
    }

    public Project(String name, ProjectState projectState, List<Topic> topics) {
        this.name = name;
        this.projectState = projectState;
        this.topics = topics;
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

    public void addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
        }
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public void setProjectState(ProjectState projectState) {
        this.projectState = projectState;
    }

    public String getProcessingError() {
        return processingError;
    }

    public void setProcessingError(String processingError) {
        this.processingError = processingError;
    }
}
