package idm3.project.gallery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProjectID")
    private Long ProjectID;

    @Column(name = "ProjectName", length = 255, nullable = false)
    private String ProjectName;

    @Column(name = "ProjectDescription", length = 1000)
    private String ProjectDescription;

    @Column(name = "ProjectHeroImage", length = 255)
    private String ProjectHeroImage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UserID")
    private User UserID;

    @Column(name = "Category", length = 255)
    private String Category;

    @Column(name = "ProjectDescriptionSummary", length = 255)
    private String ProjectDescriptionSummary;

    @Column(name = "CreationDate", length = 255)
    //private LocalDateTime CreationDate;
    private java.sql.Date CreationDate;

    @Column(name = "additionalDoc", length = 255)
    private String additionalDoc;




    @PrePersist
    protected void onCreate() {
        if (this.CreationDate == null) {
            this.CreationDate = new java.sql.Date(System.currentTimeMillis());
        }
    }


    public Project(Long ProjectID, String ProjectName, String ProjectDescription, String ProjectHeroImage) {
        this.ProjectID = ProjectID;
        this.ProjectName = ProjectName;
        this.ProjectDescription = ProjectDescription;
        this.ProjectHeroImage = ProjectHeroImage;
        this.UserID = UserID;
        this.Category = Category;
        this.ProjectDescriptionSummary = ProjectDescriptionSummary;
        this.CreationDate = CreationDate;
        this.additionalDoc = additionalDoc;
    }

    public Project(Long ProjectID, String ProjectName, String ProjectDescription, String ProjectHeroImage, User UserID, String Category, String ProjectDescriptionSummary, java.sql.Date CreationDate, String additionalDoc) {
        this.ProjectID = ProjectID;
        this.ProjectName = ProjectName;
        this.ProjectDescription = ProjectDescription;
        this.ProjectHeroImage = ProjectHeroImage;
        this.UserID = UserID;
        this.Category = Category;
        this.ProjectDescriptionSummary = ProjectDescriptionSummary;
        this.CreationDate = CreationDate;
        this.additionalDoc = additionalDoc;
    }

    public Project() {
    }

    public Long getProjectId() {
        return this.ProjectID;
    }

    public String getProjectName() {
        return this.ProjectName;
    }

    public String getProjectDescription() {
        return this.ProjectDescription;
    }

    public String getProjectImage() {
        return this.ProjectHeroImage;
    }

    public void setProjectId(Long ProjectId) {
        this.ProjectID = ProjectId;
    }

    public void setProjectName(String ProjectName) {
        this.ProjectName = ProjectName;
    }

    public void setProjectDescription(String ProjectDescription) {
        this.ProjectDescription = ProjectDescription;
    }

    public void setProjectImage(String ProjectImage) {
        this.ProjectHeroImage = ProjectImage;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Project)) return false;
        final Project other = (Project) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$ProjectId = this.getProjectId();
        final Object other$ProjectId = other.getProjectId();
        if (this$ProjectId == null ? other$ProjectId != null : !this$ProjectId.equals(other$ProjectId)) return false;
        final Object this$ProjectName = this.getProjectName();
        final Object other$ProjectName = other.getProjectName();
        if (this$ProjectName == null ? other$ProjectName != null : !this$ProjectName.equals(other$ProjectName))
            return false;
        final Object this$ProjectDescription = this.getProjectDescription();
        final Object other$ProjectDescription = other.getProjectDescription();
        if (this$ProjectDescription == null ? other$ProjectDescription != null : !this$ProjectDescription.equals(other$ProjectDescription))
            return false;
        final Object this$ProjectHeroImage = this.getProjectImage();
        final Object other$ProjectHeroImage = other.getProjectImage();
        if (this$ProjectHeroImage == null ? other$ProjectHeroImage != null : !this$ProjectHeroImage.equals(other$ProjectHeroImage))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Project;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $ProjectId = this.getProjectId();
        result = result * PRIME + ($ProjectId == null ? 43 : $ProjectId.hashCode());
        final Object $ProjectName = this.getProjectName();
        result = result * PRIME + ($ProjectName == null ? 43 : $ProjectName.hashCode());
        final Object $ProjectDescription = this.getProjectDescription();
        result = result * PRIME + ($ProjectDescription == null ? 43 : $ProjectDescription.hashCode());
        final Object $ProjectImage = this.getProjectImage();
        result = result * PRIME + ($ProjectImage == null ? 43 : $ProjectImage.hashCode());
        return result;
    }

    public String toString() {
        return "Project(ProjectId=" + this.getProjectId() + ", ProjectName=" + this.getProjectName() + ", ProjectDescription=" + this.getProjectDescription() + ", ProjectImage=" + this.getProjectImage() + ")";
    }

    public String getProjectHeroImage() {
        return this.ProjectHeroImage;
    }

    public User getUserID() {
        return this.UserID;
    }

    public String getCategory() {
        return this.Category;
    }

    public String getProjectDescriptionSummary() {
        return this.ProjectDescriptionSummary;
    }

    public java.sql.Date getCreationDate() {
        return this.CreationDate;
    }

    public String getAdditionalDoc() {
        return this.additionalDoc;
    }

    public void setProjectHeroImage(String ProjectHeroImage) {
        this.ProjectHeroImage = ProjectHeroImage;
    }

    public void setUserID(User UserID) {
        this.UserID = UserID;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public void setProjectDescriptionSummary(String ProjectDescriptionSummary) {
        this.ProjectDescriptionSummary = ProjectDescriptionSummary;
    }

    public void setCreationDate(java.sql.Date CreationDate) {
        this.CreationDate = CreationDate;
    }

    public void setAdditionalDoc(String additionalDoc) {
        this.additionalDoc = additionalDoc;
    }
}


//    public Long getProjectId() { return ProjectID; }
//    public void setProjectId(Long projectId) { this.ProjectID = projectId; }
//
//    public String getProjectTitle() { return ProjectName; }
//    public void setProjectTitle(String projectTitle) { this.ProjectName = projectTitle; }
//
//    public String getProjectDescription() { return ProjectDescription; }
//    public void setProjectDescription(String ProjectDescription) { this.ProjectDescription = ProjectDescription; }
//
//    public User getUserID() { return UserID; }
//    public void setUserID(User UserID) { this.UserID = UserID; }
//
//    public String getCategory() { return Category; }
//    public void setCategory(String Category) { this.Category = ProjectHeroImage; }
//
//    public String getProjectDescriptionSummary() { return ProjectDescriptionSummary; }
//    public void setProjectDescriptionSummary(String ProjectDescriptionSummary) { this.ProjectDescriptionSummary = ProjectDescriptionSummary; }
//
//    public LocalDateTime getCreationDate() { return CreationDate; }
//    public void setCreationDate(LocalDateTime CreationDate) { this.CreationDate = CreationDate; }
//
//    public String getAdditionalDoc() { return additionalDoc; }
//    public void setAdditionalDoc(String additionalDoc) { this.additionalDoc = additionalDoc; }
//
//    public String getProjectImage() { return ProjectHeroImage; }
//    public void setProjectImage(String ProjectHeroImage) { this.ProjectHeroImage = ProjectHeroImage; }

//    @Override
//    public String toString() {
//        return "Project(" +
//                "projectId=" + ProjectID +
//                ", projectTitle='" + ProjectName + '\'' +
//                ", projectDescription='" + ProjectDescription + '\'' +
//                ", projectImage='" + ProjectHeroImage + '\'' +
//                ", userID='" + UserID + '\'' +
//                ", category='" + Category + '\'' +
//                ", projectDescriptionSummary='" + ProjectDescriptionSummary + '\'' +
//                ", creationDate='" + CreationDate + '\'' +
//                ", additionalDoc='" + additionalDoc + '\'' +
//                ')';
//    }
//}
