package idm3.project.gallery.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "projects")
@Entity
@Table(name="showcase")
public class Showcase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ShowcaseId")
  private long showcaseId;

  @Column(name = "Name")
  private String name;

  @Column(name = "Description")
  private String description;

  @Column(name = "Image")
  private String image;

  @Column(name = "Status")
  private String status;

  @ManyToMany
  @JoinTable(
          name = "showcaseproject", // ✅ use your existing table
          joinColumns = @JoinColumn(name = "ShowcaseId"),
          inverseJoinColumns = @JoinColumn(name = "ProjectId")
  )
  private List<Project> projects;
}
