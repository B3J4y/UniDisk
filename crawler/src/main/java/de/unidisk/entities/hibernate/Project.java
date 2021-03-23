package de.unidisk.entities.hibernate;

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

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private ProjectSubtype projectSubtype;

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

    public Integer getParentProjectId() {
        return parentProjectId;
    }

    public boolean isSubproject(){
        return projectSubtype != null;
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
        return subprojects;
    }

    public void setSubprojects(List<Project> subprojects) {
        this.subprojects = subprojects;
    }
}
