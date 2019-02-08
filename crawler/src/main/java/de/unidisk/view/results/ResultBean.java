package de.unidisk.view.results;

import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Objects;

@ManagedBean
public class ResultBean implements Serializable {
    private String variable;
    private String website;

    public ResultBean(String variable, String website) {
        this.variable = variable;
        this.website = website;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResultBean that = (ResultBean) o;
        return Objects.equals(getVariable(), that.getVariable()) && Objects.equals(getWebsite(), that.getWebsite());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getVariable(), getWebsite());
    }
}
