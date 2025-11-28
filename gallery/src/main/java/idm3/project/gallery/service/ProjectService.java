package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@Service
public class ProjectService {
    private static final String UPLOAD_DIR = "/gallery/src/main/resources/static.assets/images/projects/thumbnail/";

    @Autowired
    private ProjectRepository projectRepo;

    public Project saveProject(Project project) {
        // ✅ Minimal fix: preserve existing CreationDate when editing
        if (project.getProjectId() != null) {
            Optional<Project> existing = projectRepo.findById(project.getProjectId());
            if (existing.isPresent()) {
                // keep the old creation date instead of overwriting with null
                project.setCreationDate(existing.get().getCreationDate());
            }
        } else {
            // for new projects, set current date
            project.setCreationDate(new java.sql.Date(System.currentTimeMillis()));
        }

        return projectRepo.save(project);
    }

    public List<Project> findAllProjects() {
        return (List<Project>) projectRepo.findAll();
    }

    public Optional<Project> findOne(long id) {
        return projectRepo.findById(id);
    }

    public void deleteById(long id) {
        if (!projectRepo.existsById(id)) {
            throw new EntityNotFoundException("Project not found with id " + id);
        }
        projectRepo.deleteById(id);
    }

    public Project updateProject(Project updatedProject) {
        Optional<Project> existing = projectRepo.findById(updatedProject.getProjectId());
        if (existing.isPresent()) {
            Project project = existing.get();
            project.setProjectName(updatedProject.getProjectName());
            project.setProjectDescription(updatedProject.getProjectDescription());
            project.setProjectHeroImage(updatedProject.getProjectHeroImage());
            return projectRepo.save(project);
        } else {
            throw new EntityNotFoundException("Project not found with id " + updatedProject.getProjectId());
        }


    }
    public void uploadProjectHeroPicture(Project project, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // Ensure folder exists
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Name file uniquely using user ID
            String filename = project.getProjectId() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR, filename);

            // Save file to static folder
            Files.write(path, file.getBytes());

            // Store filename (relative to profile/ directory)
            project.setProjectHeroImage(filename);
            projectRepo.save(project);
        }
    }
    public List<Project> searchProjects(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return (List<Project>) projectRepo.findAll();
        }
        return projectRepo.findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
                keyword, keyword);
    }
}


