package de.unidisk.view.project;

import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.view.model.KeywordItem;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = KeywordTable.BEAN_NAME)
@ViewScoped
public class KeywordTable implements Serializable {
    public static final String BEAN_NAME = "keywordTable";

    private List<KeywordItem> keywords;
    private String selectedProjectId;
    private boolean canEditProject = false;

    public KeywordTable() {
    }

    @ManagedProperty("#{projectRepository}")
    private IProjectRepository projectRepository;

    @PostConstruct
    public void init() {
        keywords = new ArrayList<>();
    }

    public void setProject(String projectId){
        selectedProjectId = projectId;
        final List<KeywordItem> keywords = projectRepository.getProjectKeywords(projectId);
        keywords.add(0, new KeywordItem(null,null, null));
        this.canEditProject = projectRepository.canEdit(projectId);
        this.keywords = keywords;
    }

    public IProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public void addKeyword(){

        /*final KeywordItem inputKeyword = keywords.get(0);
        final Keyword keyword = projectRepository.addKeyword(inputKeyword.copy(null));
        final KeywordItem newKeyword = inputKeyword.copy(String.valueOf(keyword.getId()));

        inputKeyword.setKeyword(null);
        inputKeyword.setVariable(null);
        System.out.println("new keyword id: " + newKeyword.getId());
        this.keywords.add(newKeyword);*/
    }

    public void deleteKeyword(KeywordItem item){
        this.keywords.removeIf((keyword) -> keyword.getId() != null && keyword.getId().equals(item.getId()));

    }

    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<KeywordItem> getKeywords() {
        return keywords;
    }

    public boolean projectIsSet(){
        return selectedProjectId != null;
    }

    public boolean canEdit(){
        return this.canEditProject;
    }
}