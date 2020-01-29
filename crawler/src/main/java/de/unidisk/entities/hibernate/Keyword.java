package de.unidisk.entities.hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Keyword")
public class Keyword implements Input {
    @Id
    @GeneratedValue
    int id;


    private int topicId;
    private String name;


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
}
