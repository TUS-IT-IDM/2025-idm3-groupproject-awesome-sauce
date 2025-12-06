package idm3.project.gallery.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@Entity

@Table(name="project")

public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectID")

    private Long projectID; // 🔧 FIXED (field name should start lowercase)


    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Project name must not exceed 255 characters")
    @Column(name = "ProjectName", length = 255, nullable = false)
    private String projectName; // 🔧 FIXED

    @NotBlank(message = "Product description is required")
    @Size(max = 255, message = "Project description must not exceed 255 characters")
    @Column(name = "ProjectDescription", length = 1000)
    private String projectDescription; // 🔧 FIXED

    @NotNull(message = "Image is required")
    @Column(name = "ProjectHeroImage", length = 255)
    private String projectHeroImage; // 🔧 FIXED

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserID")
    private User userID; // 🔧 FIXED

    @NotBlank(message = "Product category is required")
    @Column(name = "Category", length = 255)
    private String category; // 🔧 FIXED

    @NotBlank(message = "Product description summary is required")
    @Column(name = "ProjectDescriptionSummary", length = 255)
    private String projectDescriptionSummary; // 🔧 FIXED

    @Column(name = "CreationDate", length = 255)
    private java.sql.Date creationDate; // 🔧 FIXED

    @Column(name = "additionalDoc", length = 255)
    private String additionalDoc;


    @PrePersist
    protected void onCreate() {
        if (this.creationDate == null) {
            this.creationDate = new java.sql.Date(System.currentTimeMillis());
        }
    }

    // ---- Constructors ----

    public Project(Long projectID, String projectName, String projectDescription, String projectHeroImage) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectHeroImage = projectHeroImage;
    }

    public Project(Long projectID, String projectName, String projectDescription, String projectHeroImage,
                   User userID, String category, String projectDescriptionSummary,
                   java.sql.Date creationDate, String additionalDoc) {

        this.projectID = projectID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectHeroImage = projectHeroImage;
        this.userID = userID;
        this.category = category;
        this.projectDescriptionSummary = projectDescriptionSummary;
        this.creationDate = creationDate;
        this.additionalDoc = additionalDoc;
    }

    public Project() {}


    // ---- Getters & Setters (all corrected) ----

    public Long getProjectId() {
        return projectID; // 🔧 FIXED
    }

    public void setProjectId(Long projectID) {
        this.projectID = projectID; // 🔧 FIXED
    }

    public String getProjectName() {
        return projectName; // 🔧 FIXED
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName; // 🔧 FIXED
    }

    public String getProjectDescription() {
        return projectDescription; // 🔧 FIXED
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription; // 🔧 FIXED
    }

    public String getProjectHeroImage() {
        return projectHeroImage; // 🔧 FIXED
    }

    public void setProjectHeroImage(String projectHeroImage) {
        this.projectHeroImage = projectHeroImage; // 🔧 FIXED
    }

    public User getUserID() {
        return userID; // 🔧 FIXED
    }

    public void setUserID(User userID) {
        this.userID = userID; // 🔧 FIXED
    }

    public String getCategory() {
        return category; // 🔧 FIXED
    }

    public void setCategory(String category) {
        this.category = category; // 🔧 FIXED
    }

    public String getProjectDescriptionSummary() {
        return projectDescriptionSummary; // 🔧 FIXED
    }

    public void setProjectDescriptionSummary(String projectDescriptionSummary) {
        this.projectDescriptionSummary = projectDescriptionSummary; // 🔧 FIXED
    }

    public java.sql.Date getCreationDate() {
        return creationDate; // 🔧 FIXED
    }

    public void setCreationDate(java.sql.Date creationDate) {
        this.creationDate = creationDate; // 🔧 FIXED
    }

    public String getAdditionalDoc() {
        return additionalDoc;
    }

    public void setAdditionalDoc(String additionalDoc) {
        this.additionalDoc = additionalDoc;
    }


    // ---- equals(), hashCode(), toString() left unchanged; they will still work ----







    @Override
    public String toString() {
        return "Project{" +
                "projectId=" + projectID +
                ", projectName='" + projectName + '\'' +
                ", projectDescription='" + projectDescription + '\'' +
                ", user=" + userID +
                ", category='" + category + '\'' +
                ", projectDescriptionSummary='" + projectDescriptionSummary + '\'' +
                ", projectHeroImage='" + projectHeroImage + '\'' +
                ", creationDate=" + creationDate +
                ", additionalDoc='" + additionalDoc + '\'' +
                '}';
    }

}



