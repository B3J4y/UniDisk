package de.unidisk.view;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "projectService")
public class ProjectService {

    private PodamFactory factory = new PodamFactoryImpl();

    public List<Project> getProjects(int i) {
        ArrayList<Project> projects = new ArrayList<>();
        for (int j = 0; j<i;j++) {
            Project project = factory.manufacturePojo(Project.class);
            projects.add(project);
        }
        return projects;
    }

    public List<String> getStatuss() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Project> projects = getProjects(12);
        for (Project project : projects) {
            arrayList.add(project.getStatus());
        }
        return arrayList;
    }

    public List<String> getNames() {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Project> projects = getProjects(12);
        for (Project project : projects) {
            arrayList.add(project.getName());
        }
        return arrayList;
    }
}
