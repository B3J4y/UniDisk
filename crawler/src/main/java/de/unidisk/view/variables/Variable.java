package de.unidisk.view.variables;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Objects;

@ManagedBean
public class Variable implements Serializable {
    private String name;
    private String keyword;


    public Variable(String name, String status) {
        this.name = name;
        this.keyword = keyword;
    }

    public Variable() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String status) {
        this.keyword = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Variable variable = (Variable) o;
        return Objects.equals(getName(), variable.getName()) && Objects.equals(getKeyword(), variable.getKeyword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getKeyword());
    }
}
