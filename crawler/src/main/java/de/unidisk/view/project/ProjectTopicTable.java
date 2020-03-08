package de.unidisk.view.project;

import de.unidisk.view.MessagingCenter;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.contracts.repositories.IProjectRepository;
import de.unidisk.contracts.repositories.ITopicRepository;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = ProjectTopicTable.BEAN_NAME)
@ViewScoped
public class ProjectTopicTable {
    public static final String BEAN_NAME = "projectTopicTable";

    private Project selectedProject;
    private Topic selectedTopic;

    private String newTopic;
    private String topicError;
    private String newKeyword;
    private String newKeywordError;


    @ManagedProperty("#{projectRepository}")
    private IProjectRepository projectRepository;

    @ManagedProperty("#{topicRepository}")
    private ITopicRepository topicRepository;

    public ProjectTopicTable(){

    }

    @PostConstruct
    public void init() {
        MessagingCenter.getInstance().subscribe(this,"ProjectDeleted",(String projektName) -> {
            if(selectedProject == null)
                return null;

            if(selectedProject.getName().equals(projektName)){
                selectedProject = null;
                selectedTopic = null;
            }
            return null;
        });
    }

    @PreDestroy
    public void destroy() {
        MessagingCenter.getInstance().unsubscribe(this,"ProjectDeleted");
    }


    public void setProject(String projectId){

        selectedProject = projectRepository.getProject(projectId);
        if(selectedProject == null){

        }else {
            if (selectedProject.getTopics().size() > 0)
                setSelectedTopic(selectedProject.getTopics().get(0));
            else
                selectedTopic = null;
        }
    }

    public List<Topic> getTopics(){
        return selectedProject.getTopics();
    }


    public void setSelectedTopic(Topic topic){
        this.selectedTopic = topic;

        if(true)
            return;
        if(topic != selectedTopic){
            selectedTopic = topic;

            //getKeywordsOfSelectedTopic();
        }

    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public Topic getSelectedTopic() {
        return selectedTopic;
    }

    public List<Keyword> getSelectedTopicKeywords() {

        return selectedTopic.getKeywords();
    }

    public boolean projectIsSet(){
        return selectedProject != null;
    }

    public boolean isTopicSet() {
        return selectedTopic != null;
    }


    public String getNewTopic() {
        return newTopic;
    }

    public void setNewTopic(String newTopic) {
        this.newTopic = newTopic;
    }

    public void addTopic(){

        if(selectedProject.getTopics().stream().filter(t -> t.getName().equals(newTopic)).count() != 0){
            topicError = "Thema existiert bereits";
        }else if(newTopic == null || newTopic.trim().equals("")){
            topicError = "Das Thema darf nicht leer sein.";
        }
        else{
            final Topic topic = topicRepository.createTopic( selectedProject.getId(),newTopic);
            if(topic == null){

                topicError = "Thema konnte nicht erstellt werden.";
                return;
            }
            selectedProject.addTopic(topic);
            newTopic = null;
            topicError  = null;
        }
    }

    public void deleteSelected(){

        if(selectedTopic == null)
            return;

        topicRepository.deleteTopic(selectedTopic.getId());
        boolean removed = selectedProject.getTopics().remove(selectedTopic);

        selectedTopic = null;

    }

    public void deleteTopic(int topicId){

        topicRepository.deleteTopic(topicId);
        boolean removed = selectedProject.getTopics().removeIf(t  -> t.getId() == topicId);
        if(selectedTopic != null && selectedTopic.getId() == topicId)
            selectedTopic = null;
    }

    public void deleteKeyword(int keywordId){

        topicRepository.deleteKeyword(keywordId);
        if(selectedTopic != null)
            selectedTopic.getKeywords().removeIf(k -> k.getId() == keywordId);
    }

    public void removeTopic(Topic topic){
        selectedProject.getTopics().remove(topic);

    }

    public void addKeyword(){

        if(selectedTopic.getKeywords() == null)
            selectedTopic.setKeywords(new ArrayList<>());
        if(selectedTopic.getKeywords().contains(newKeyword)){
            newKeywordError = "Das Stichwort wurde dem Thema bereits zugewiesen.";
        }else if(newKeyword == null || newKeyword.trim().equals("")){
            newKeywordError = "Ein Stichwort darf nicht leer sein";
        }else{
            Keyword keyword = topicRepository.addKeyword(selectedTopic.getId(),newKeyword);
            if(keyword == null){
                newKeywordError = "Stichwort mit gleichem Namen existiert bereits.";
                return;
            }
            selectedTopic.getKeywords().add(keyword);
            newKeyword = null;
            newKeywordError = null;
        }
    }

    public void removeKeyword(int keywordId){
        topicRepository.deleteKeyword(keywordId);
        selectedTopic.getKeywords().removeIf(k  -> k.getId() == keywordId);
    }

    public void onRowSelect(SelectEvent event){


    }

    public void onRowUnselect(UnselectEvent event){

    }

    public void setSelectedProject(Project selectedProject) {
        this.selectedProject = selectedProject;
    }

    public String getTopicError() {
        return topicError;
    }

    public void setTopicError(String topicError) {
        this.topicError = topicError;
    }

    public String getNewKeyword() {
        return newKeyword;
    }

    public void setNewKeyword(String newKeyword) {
        this.newKeyword = newKeyword;
    }

    public IProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ITopicRepository getTopicRepository() {
        return topicRepository;
    }

    public void setTopicRepository(ITopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public String getNewKeywordError() {
        return newKeywordError;
    }

    public boolean canEdit(){
        return this.selectedProject != null && this.selectedProject.getProjectState() == ProjectState.IDLE;
    }
}
