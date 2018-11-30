package de.unidisk.view;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Objects;

@ManagedBean
public class Project implements Serializable {
    private String name;
    private String status;


    public Project(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Project project = (Project) o;
        return Objects.equals(getName(), project.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
