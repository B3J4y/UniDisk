package de.unidisk.view.results;

import com.google.gson.Gson;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.University;
import de.unidisk.repositories.contracts.IProjectRepository;
import de.unidisk.view.model.MapLegendItem;
import de.unidisk.view.model.MapMarker;
import org.primefaces.PrimeFaces;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean(name = "projectResultTable")
@ViewScoped
public class ProjectResultTable {

    @ManagedProperty("#{projectRepository}")
    private IProjectRepository projectRepository;
    private List<Result> results;
    private String projectId;
    private HashMap<Integer,String> topicIcons;
    private List<MapLegendItem> mapLegendItems;
    private String setupError;

    private boolean projectHasResults = false;
    private String projectStatusMessage;

    public void load(String projectId){
        this.projectId = projectId;
        results = projectRepository.getResults(this.projectId);
        topicIcons = new HashMap<Integer, String>();

        final Project p = projectRepository.getProject(projectId);

        if(p == null){
            setupError = "Projekt existiert nicht.";
        }
        else {
            projectHasResults = p.getProjectState() == ProjectState.FINISHED;
            if(!projectHasResults){
                if(p.getProjectState() == ProjectState.RUNNING){
                    projectStatusMessage = "Das Projekt wird noch bearbeitet.";
                }else if(p.getProjectState() == ProjectState.ERROR){
                    projectStatusMessage = "Beim Bearbeiten des Projekts trat ein Fehler auf.";
                }else if(p.getProjectState() == ProjectState.WAITING){
                    projectStatusMessage = "Die Bearbeitung des Projekts steht noch aus.";
                }
            }else{
                projectStatusMessage = null;
            }

            mapLegendItems = p.getTopics().stream().map(t -> {
                final String url = "https://img.icons8.com/material/4ac144/256/user-male.png";
                topicIcons.put(t.getId(), url);
                return new MapLegendItem(url, t.getName());
            }).collect(Collectors.toList());

            RefreshMap();
        }
    }


    void RefreshMap(){
        final List<MapMarker> marker = projectRepository.getMarker(this.projectId);
        marker.forEach(m -> {
            final String url = topicIcons.get(m.getTopicId());

            m.setIconUrl("https://img.icons8.com/material/4ac144/256/user-male.png");
        });
        final Gson gson = new Gson();
        String jsonMarker = gson.toJson(marker);
        System.out.println(jsonMarker);
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

    public List<MapLegendItem> getMapLegendItems() {
        return mapLegendItems;
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
}
