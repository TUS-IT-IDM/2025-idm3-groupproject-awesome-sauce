// src/main/java/idm3/project/gallery/model/SavedProject.java
package idm3.project.gallery.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(
        name = "savedproject",
        uniqueConstraints = @UniqueConstraint(name = "uq_employer_project", columnNames = {"EmployerUserId","ProjectID"})
)
public class SavedProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SavedId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EmployerUserId", nullable = false)   // matches your DB column
    private User employer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProjectID", nullable = false)         // matches your DB column
    private Project project;

    @Column(name = "Note", length = 1000)
    private String note;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
