package idm3.project.gallery.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="showcase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

  // 🔥 ADD THIS
  @ManyToMany
  @JoinTable(
          name = "showcase",
          joinColumns = @JoinColumn(name = "showcase_id"),
          inverseJoinColumns = @JoinColumn(name = "project_id")
  )
  private List<Project> projects;
}


