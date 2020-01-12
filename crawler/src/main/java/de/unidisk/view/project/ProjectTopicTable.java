package de.unidisk.view.project;

import de.unidisk.MessagingCenter;

import de.unidisk.entities.hibernate.Keyword;
import de.unidisk.entities.hibernate.Project;
import de.unidisk.entities.hibernate.ProjectState;
import de.unidisk.entities.hibernate.Topic;
import de.unidisk.repositories.contracts.IProjectRepository;
import de.unidisk.repositories.contracts.ITopicRepository;
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
            System.out.println("AddRowView: new project created");
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
        System.out.println("Set selected project to " + projectId);
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

        if(topic != null)
            System.out.println("Set topic to " + topic.getName());
        else
            System.out.println("Set topic to null");
    }

    public Project getSelectedProject() {
        return selectedProject;
    }

    public Topic getSelectedTopic() {
        return selectedTopic;
    }

    public List<Keyword> getSelectedTopicKeywords() {
        System.out.println("getSelectedTopicKeywords");
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
        System.out.println("add topic");
        if(selectedProject.getTopics().stream().filter(t -> t.getName().equals(newTopic)).count() != 0){
            topicError = "Thema existiert bereits";
        }else if(newTopic == null || newTopic.trim().equals("")){
            topicError = "Das Thema darf nicht leer sein.";
        }
        else{
            final Topic topic = topicRepository.createTopic( selectedProject.getId(),newTopic);
            if(topic == null){
                System.out.println("");
                topicError = "Thema konnte nicht erstellt werden.";
                return;
            }
            selectedProject.addTopic(topic);
            newTopic = null;
            topicError  = null;
        }
    }

    public void deleteSelected(){
        System.out.println("topic is set " + isTopicSet());
        if(selectedTopic == null)
            return;
        System.out.println("delete selected topic");
        topicRepository.deleteTopic(selectedTopic.getId());
        boolean removed = selectedProject.getTopics().remove(selectedTopic);

        selectedTopic = null;
        System.out.println("topic removed " + removed);
    }

    public void deleteTopic(int topicId){
        System.out.println("delete topic with id " + topicId);
        topicRepository.deleteTopic(topicId);
        boolean removed = selectedProject.getTopics().removeIf(t  -> t.getId() == topicId);
        if(selectedTopic != null && selectedTopic.getId() == topicId)
            selectedTopic = null;
    }

    public void deleteKeyword(int keywordId){
        System.out.println("delete keyword with id " + keywordId);
        topicRepository.deleteKeyword(keywordId);
        if(selectedTopic != null)
            selectedTopic.getKeywords().removeIf(k -> k.getId() == keywordId);
    }

    public void removeTopic(Topic topic){
        selectedProject.getTopics().remove(topic);

    }

    public void addKeyword(){
        System.out.println("add keyword "+ newKeyword);
        System.out.println("topic is set " + isTopicSet());
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

       /* Topic topic = (Topic) event.getObject();
        System.out.println("Set selected topic to" + topic.getName());
        selectedTopic = topic;
        selectedTopicKeywords = new ArrayList<>();*/
        //getKeywordsOfSelectedTopic();
    }

    public void onRowUnselect(UnselectEvent event){
        //selectedTopic = null;
        //selectedTopicKeywords = null;
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
        return this.selectedProject != null && this.selectedProject.getProjectState() == ProjectState.WAITING;
    }
}
