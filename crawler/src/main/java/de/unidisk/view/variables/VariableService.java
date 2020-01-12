package de.unidisk.view.variables;

import de.unidisk.dao.ProjectDAO;
import de.unidisk.entities.hibernate.Project;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "variableService")
public class VariableService {

    private PodamFactory factory = new PodamFactoryImpl();


    @ManagedProperty("#{variablesView}")
    private VariablesView variablesView;

    public List<Variable> getVariables(int i) {

        ProjectDAO projectDAO = new ProjectDAO();
        Project project = projectDAO
                .findProject(variablesView.getProjectSelected()).get();
        //List<Topic> topics = project.getTopics();
        //topics.get(0).get

        ArrayList<Variable> variables = new ArrayList<>();
        for (int j = 0; j<i;j++) {
            Variable variable = factory.manufacturePojo(Variable.class);
            variables.add(variable);
        }
        return variables;
    }

    public List<String> getKeywordss() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Variable> variables = getVariables(12);
        for (Variable variable : variables) {
            arrayList.add(variable.getKeyword());
        }
        return arrayList;
    }

    public List<String> getNames() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Variable> projects = getVariables(12);
        for (Variable project : projects) {
            arrayList.add(project.getName());
        }
        return arrayList;
    }

    public VariablesView getVariablesView() {
        return variablesView;
    }

    public void setVariablesView(VariablesView variablesView) {
        this.variablesView = variablesView;
    }
}
