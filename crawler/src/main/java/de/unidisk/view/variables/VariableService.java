package de.unidisk.view.variables;

import de.unidisk.view.project.Project;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "variableService")
public class VariableService {

    private PodamFactory factory = new PodamFactoryImpl();



    public List<Variable> getVariables(int i) {
        ArrayList<Variable> variables = new ArrayList<>();
        for (int j = 0; j<i;j++) {
            Variable variable = factory.manufacturePojo(Variable.class);
            variables.add(variable);
        }
        return variables;
    }

    public List<String> getStatuss() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Variable> variables = getVariables(12);
        for (Variable variable : variables) {
            arrayList.add(variable.getStatus());
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
}
