package idm3.project.gallery.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "showcase")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Showcase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ShowcaseId")
  private Long showcaseId;

  private String name;
  private String description;
  private String status;
  private String image;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CreatedBy", nullable = false)
  @JsonIgnoreProperties({"password", "email", "roles", "showcases"}) // prevent recursion
  private User createdBy;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "showcaseproject",
          joinColumns = @JoinColumn(name = "ShowcaseId"),
          inverseJoinColumns = @JoinColumn(name = "ProjectID")
  )
  @JsonIgnoreProperties({"user", "showcase"}) // don't include user or showcase inside project JSON
  private List<Project> projects;

  @Column(name = "CreatedAt", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();
}
