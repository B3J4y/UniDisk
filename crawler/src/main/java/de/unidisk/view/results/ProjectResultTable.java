package de.unidisk.view.results;

import com.google.gson.Gson;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.contracts.repositories.IProjectRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ManagedBean(name = "projectResultTable")
@ViewScoped
public class ProjectResultTable {

    static private final Logger logger = LogManager.getLogger(ProjectResultTable.class.getName());

    @ManagedProperty("#{projectRepository}")
    private IProjectRepository projectRepository;
    private List<Result> results = new ArrayList<>();
    private String projectId;
    private HashMap<String,Boolean> vibisibilityMap = new HashMap<>();

    private String setupError;
    private Project project;

    private boolean projectHasResults = false;
    private String projectStatusMessage;
    private boolean projectExists = false;

    private void setProjectNotExisting(){
        results  = new ArrayList<>();
        setupError = "Projekt existiert nicht.";
        projectExists = false;
    }

    public void load(String projectId){
        if(this.projectId == projectId && results != null && results.size() > 0){
            return;
        }
        this.projectId = projectId;
        if(projectId == null){
            setProjectNotExisting();
            return;
        }

        final Optional<Project> p = projectRepository.getProject(projectId);

        if(!p.isPresent()){
            setProjectNotExisting();
            return;
        }else{
            this.project = p.get();
        }

        if(project.getProcessingError() !=null){
            System.out.println(project.getProcessingError());
        }


        projectExists = true;
        projectHasResults = project.getProjectState() == ProjectState.FINISHED;
        if(!projectHasResults){
            if(project.getProjectState() == ProjectState.RUNNING){
                projectStatusMessage = "Das Projekt wird noch bearbeitet.";
            }else if(project.getProjectState() == ProjectState.ERROR){
                projectStatusMessage = "Beim Bearbeiten des Projekts trat ein Fehler auf.";
            }else if(project.getProjectState() == ProjectState.WAITING){
                projectStatusMessage = "Die Bearbeitung des Projekts steht noch aus.";
            }
        }else{
            projectStatusMessage = null;
        }

        results = projectRepository.getAllResults(this.projectId).get(0).getResults();
        vibisibilityMap.clear();
        results.forEach(r -> vibisibilityMap.put(r.getTopic(),true));
        RefreshMap(results);

    }


    void RefreshMap(List<Result> visibleResults){
        final Gson gson = new Gson();
        String jsonMarker = gson.toJson(visibleResults);
        PrimeFaces.current().executeScript("refreshMap("+jsonMarker+")");
    }

    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Result> getResults() {
        return results;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSetupError() {
        return setupError;
    }

    public void setSetupError(String setupError) {
        this.setupError = setupError;
    }

    public boolean hasResults(){
        return projectHasResults;
    }

    public String getProjectStatusMessage() {
        return projectStatusMessage;
    }

    public boolean projectExists(){
        return this.projectExists;
    }

    public void toggleTopicVisibility(String topicName){
        vibisibilityMap.put(topicName,!vibisibilityMap.get(topicName));
        List<Result> visibleTopics = results.stream().filter(r -> vibisibilityMap.get(r.getTopic())).collect(Collectors.toList());
        RefreshMap(visibleTopics);
    }

    public List<String> getTopics(){
        return this.results.stream().map(Result::getTopic).collect(Collectors.toList());
    }

    public boolean topicIsVisible(String name){
        if(vibisibilityMap.containsKey(name))
            return vibisibilityMap.get(name);
        return true;
    }

    public Project getProject() {
        return project;
    }
}
