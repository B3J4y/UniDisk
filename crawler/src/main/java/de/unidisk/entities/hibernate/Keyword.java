package de.unidisk.entities.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity(name = "Keyword")
public class Keyword implements Input {
    @Id
    @GeneratedValue
    int id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId", insertable = false, updatable = false)
    private Topic topic;
    private int topicId;
    private String name;
    private boolean isSuggestion = false;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<KeyWordScore> keyWordScores;

    @Basic
    @Column()
    @JsonIgnore()
    private java.time.Instant finishedProcessingAt;

    public Keyword() {

    }

    public Keyword(String name) {
        this();
        this.name = name;
    }


    public Keyword(String name, int topicId) {
        this();
        this.name = name;
        this.topicId = topicId;
        this.isSuggestion = false;
    }

    public Keyword(String name, int topicId, boolean isSuggestion) {
        this();
        this.name = name;
        this.topicId = topicId;
        this.isSuggestion = isSuggestion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Keyword)) return false;

        Keyword keyword = (Keyword) o;

        if (id != keyword.id) return false;
        return name != null ? name.equals(keyword.name) : keyword.name == null;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public boolean isSuggestion() {
        return isSuggestion;
    }

    public List<KeyWordScore> getKeyWordScores() {
        return keyWordScores;
    }

    public void setKeyWordScores(List<KeyWordScore> keyWordScores) {
        this.keyWordScores = keyWordScores;
    }

    public boolean finishedProcessing(){
        return finishedProcessingAt != null;
    }

    public void setFinishedProcessingAt(Instant finishedProcessingAt) {
        this.finishedProcessingAt = finishedProcessingAt;
    }
}
