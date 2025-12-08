package idm3.project.gallery.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "showcaseproject")
public class Showcaseproject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ShowcaseProjectID")
  private Long showcaseProjectId;

  // ✅ rename field to "project" for clarity and consistency
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ProjectID", nullable = false)
  private Project project;

  // ✅ rename field to "showcase" and make naming consistent
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ShowcaseID", nullable = false)
  private Showcase showcase;
}
