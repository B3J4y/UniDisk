package de.unidisk.entities.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Topic")
@Table(
        uniqueConstraints={
                @UniqueConstraint(columnNames = {"projectId", "name"}),
        }
)
public class Topic implements Serializable,Input {
    @Id
    @GeneratedValue
    private int id;


    @OneToMany(fetch = FetchType.EAGER,mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<TopicScore> topicScores;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", insertable = false, updatable = false)
    private Project project;

    private int projectId;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "topicId", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Keyword> keywords;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "topicId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ProjectRelevanceScore> relevanceScores;

    @Basic
    @Column()
    @JsonIgnore()
    private java.time.Instant finishedProcessingAt;

    public Topic() {
    }

    public Topic(String name, int projectId) {
        this.name = name;
        this.projectId = projectId;
        keywords = new ArrayList<>();
    }

    public Topic(String name, int projectId, List<Keyword> keywords) {
        this.name = name;
        this.projectId = projectId;
        this.keywords = keywords;
    }

    @Override
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

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id &&
                Objects.equals(name, topic.name);
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    public List<TopicScore> getTopicScores() {
        return topicScores;
    }


    public List<ProjectRelevanceScore> getRelevanceScores() {
        return relevanceScores;
    }

    public boolean finishedProcessing(){
        return finishedProcessingAt != null;
    }

    public Instant getFinishedProcessingAt() {
        return finishedProcessingAt;
    }

    public void setFinishedProcessingAt(Instant finishedProcessingAt) {
        this.finishedProcessingAt = finishedProcessingAt;
    }
}
