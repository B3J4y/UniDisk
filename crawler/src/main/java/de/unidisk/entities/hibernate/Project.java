package de.unidisk.entities.hibernate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        uniqueConstraints={
                @UniqueConstraint(columnNames = {"name", "userId"}),
                @UniqueConstraint(columnNames = {"parentProjectId", "projectSubtype"})
                },
        indexes =    {
                @Index(columnList = "userId", name = "project_user_id_idx"),
                @Index(columnList = "parentProjectId", name = "project_parent_idx"),
        }
)
public class Project {
    @Id
    @GeneratedValue
    private int id;

    @Column()
    private String name;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private ProjectState projectState;

    @Column(nullable = true)
    private String processingError;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Topic> topics;

    @ColumnDefault("null")
    @Column(nullable = true)
    // Using int as type doesn't seem to work because Hibernate always uses 0 instead of null as default
    private Integer parentProjectId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProjectSubtype projectSubtype;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentProjectId", insertable = false, updatable = false)
    private Project parentProject;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentProjectId", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Project> subprojects;

    public Project() {
    }

    public Project(String name, ProjectState projectState, List<Topic> topics) {
        this.name = name;
        this.projectState = projectState;
        this.topics = topics;
    }

    public Project(String name) {
        this.name = name;
    }

    @PrePersist
    public void prePersist() {
        if(projectSubtype == null)
        {
            projectSubtype = ProjectSubtype.DEFAULT;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
        }
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    @JsonIgnore
    public ProjectState getSubprojectState() {
        final boolean finishedSubprojects =  getSubprojects().stream().map(p -> p.getProjectState() == ProjectState.FINISHED).reduce(true,(v1, v2) -> v1 && v2);
        return getProjectState() == ProjectState.FINISHED && !finishedSubprojects ? ProjectState.RUNNING : getProjectState();
    }

    public void setProjectState(ProjectState projectState) {
        this.projectState = projectState;
    }

    public String getProcessingError() {
        return processingError;
    }

    public void setProcessingError(String processingError) {
        this.processingError = processingError;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean canEdit(){
        return this.projectState == ProjectState.IDLE;
    }

    public boolean finishedProcessing(){
        return this.projectState == ProjectState.FINISHED;
    }

    public Integer getParentProjectId() {
        return parentProjectId;
    }

    public boolean isSubproject(){
        return projectSubtype != ProjectSubtype.DEFAULT;
    }

    public void setParentProjectId(int parentProjectId) {
        this.parentProjectId = parentProjectId;
    }

    public ProjectSubtype getProjectSubtype() {
        return projectSubtype;
    }

    public void setProjectSubtype(ProjectSubtype projectSubtype) {
        this.projectSubtype = projectSubtype;
    }

    public List<Project> getSubprojects() {
        if(this.subprojects == null){
            this.subprojects = new ArrayList<>();
        }
        return subprojects;
    }

    public void setSubprojects(List<Project> subprojects) {
        this.subprojects = subprojects;
    }

    @JsonIgnore
    public Project getParentProject() {
        return parentProject;
    }
}
