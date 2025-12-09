package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.repository.ProjectRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private static final String UPLOAD_DIR =
            "/gallery/src/main/resources/static.assets/images/projects/thumbnail/";

    @Autowired
    private ProjectRepository projectRepo;


    /* ============================================================
       CREATE / UPDATE
       ============================================================ */

    public Project saveProject(Project project) {
        // ✅ Preserve existing creationDate when editing
        if (project.getProjectId() != null) {
            Optional<Project> existing = projectRepo.findById(project.getProjectId());
            existing.ifPresent(e -> project.setCreationDate(e.getCreationDate()));
        } else {
            // New project → set current date
            project.setCreationDate(new Date(System.currentTimeMillis()));
        }

        return projectRepo.save(project);
    }

    public Project save(Project project) {
        return saveProject(project);
    }

    public Project updateProject(Project updatedProject) {
        Optional<Project> existingOpt = projectRepo.findById(updatedProject.getProjectId());
        if (existingOpt.isEmpty()) {
            throw new EntityNotFoundException("Project not found with id " + updatedProject.getProjectId());
        }

        Project project = existingOpt.get();
        project.setProjectName(updatedProject.getProjectName());
        project.setProjectDescription(updatedProject.getProjectDescription());
        project.setProjectHeroImage(updatedProject.getProjectHeroImage());

        return projectRepo.save(project);
    }

    public void deleteById(long id) {
        if (!projectRepo.existsById(id)) {
            throw new EntityNotFoundException("Project not found with id " + id);
        }
        projectRepo.deleteById(id);
    }


    /* ============================================================
       IMAGE UPLOAD
       ============================================================ */

    public void uploadProjectHeroPicture(Project project, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {

            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String filename = project.getProjectId() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR, filename);

            Files.write(path, file.getBytes());

            project.setProjectHeroImage(filename);
            projectRepo.save(project);
        }
    }


    /* ============================================================
       FIND METHODS
       ============================================================ */

    public Optional<Project> findOne(long id) {
        return projectRepo.findById(id);
    }

    public Project findById(long id) {
        return projectRepo.findById(id).orElse(null);
    }

    public List<Project> findAllProjects() {
        return (List<Project>) projectRepo.findAll();
    }

    public List<Project> findAll() {
        return (List<Project>) projectRepo.findAll();
    }

    public List<Project> findAllOrderedByCreationDate() {
        return projectRepo.findAllByOrderByCreationDateDesc();
    }

    public long totalProjects() {
        return projectRepo.count();
    }

    public List<Project> recentProjects() {
        return projectRepo.findTop5ByOrderByCreationDateDesc();
    }

  
  // Possible error here
    public Project findById(long id) {
        return projectRepo.findById(id).orElse(null);
    }

    public Project save(Project project) {
        return projectRepo.save(project);
    }

//    public void deleteById(long id) {
//        projectRepo.deleteById(id);
//    }

    /* ============================================================
       SIMPLE (NON-PAGINATED) SEARCH
       ============================================================ */

    public List<Project> searchProjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return (List<Project>) projectRepo.findAll();
        }
        return projectRepo.findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
                keyword, keyword
        );
    }


    /* ============================================================
       PAGINATION + FILTERS (USED BY HOMEPAGE)
       ============================================================ */

    /**
     * ✅ Old signature kept for compatibility (no filters).
     */
    public Page<Project> getPaginatedProjects(int page) {
        return getPaginatedProjects(page, null, null, null);
    }

    /**
     *   New flexible method:
     * - keyword  → search in name/description
     * - category → filter by category
     * - sortBy   → "newest", "oldest", "az", "za" (creationDate / name)
     */
    public Page<Project> getPaginatedProjects(int page,
                                              String keyword,
                                              String category,
                                              String sortBy) {

        int size = 15;
        Pageable pageable = buildPageable(page, size, sortBy);

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        String kw = hasKeyword ? keyword.trim() : null;
        String cat = hasCategory ? category.trim() : null;

        if (!hasKeyword && !hasCategory) {
            // No filters at all → default listing
            return projectRepo.findAllByOrderByCreationDateDesc(pageable);
        } else if (hasKeyword && !hasCategory) {
            // Only keyword
            return projectRepo.searchProjectsPaginated(kw, pageable);
        } else if (!hasKeyword && hasCategory) {
            // Only category
            return projectRepo.findByCategoryIgnoreCase(cat, pageable);
        } else {
            // Both keyword + category
            return projectRepo.searchByKeywordAndCategory(kw, cat, pageable);
        }
    }

    /**
     * Used previously by homepage search – now just delegates to the new method.
     */
    public Page<Project> searchProjectsPaginated(String keyword, int page) {
        return getPaginatedProjects(page, keyword, null, null);
    }

    /* Helper to build pageable with sorting rules */
    private Pageable buildPageable(int page, int size, String sortBy) {

        Sort sort;

        if (sortBy == null || sortBy.isBlank()) {
            sort = Sort.by("creationDate").descending();
        } else {
            switch (sortBy.toLowerCase()) {
                case "oldest" -> sort = Sort.by("creationDate").ascending();
                case "az" -> sort = Sort.by("projectName").ascending();
                case "za" -> sort = Sort.by("projectName").descending();
                case "newest" -> {
                    // explicit option if you use it in UI
                    sort = Sort.by("creationDate").descending();
                }
                default -> sort = Sort.by("creationDate").descending();
            }
        }

        return PageRequest.of(page, size, sort);
    }

}
