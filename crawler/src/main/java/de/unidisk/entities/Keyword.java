package de.unidisk.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Keyword")
public class Keyword implements Input {
    @Id
    @GeneratedValue
    int id;
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Topic> topics;

    public Keyword() {
        topics = new ArrayList<>();
    }

    public Keyword(String name) {
        this();
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Keyword)) return false;

        Keyword keyword = (Keyword) o;

        if (id != keyword.id) return false;
        return name != null ? name.equals(keyword.name) : keyword.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
