package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepo;


    public List<Project> findAllOrderedByCreationDate() {
        return projectRepo.findAllByOrderByCreationDateDesc();
    }

    public List<Project> findAll() {
        return (List<Project>) projectRepo.findAll();
    }

    public long totalProjects() {
        return projectRepo.count();
    }

    public List<Project> recentProjects() {
        return projectRepo.findTop5ByOrderByCreationDateDesc();
    }

    public Project findById(long id) {
        return projectRepo.findById(id).orElse(null);
    }

    public Project save(Project project) {
        return projectRepo.save(project);
    }

    public void deleteById(long id) {
        projectRepo.deleteById(id);
    }


    /* --------------------------------------------------------
       NON-PAGINATED SEARCH (STILL USED SOMEWHERE)
    -------------------------------------------------------- */
    public List<Project> searchProjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return (List<Project>) projectRepo.findAll();
        }
        return projectRepo.findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
                keyword, keyword);
    }


    /* --------------------------------------------------------
       DEFAULT PAGINATED PROJECTS FOR HOMEPAGE
    -------------------------------------------------------- */
    public Page<Project> getPaginatedProjects(int page) {
        int size = 15; // How many per page
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return projectRepo.findAllByOrderByCreationDateDesc(pageable);
    }


    /* --------------------------------------------------------
       ✅ PAGINATED SEARCH — REQUIRED FOR HOMEPAGE SEARCH
       This is the missing method your controller needs.
    -------------------------------------------------------- */
    public Page<Project> searchProjectsPaginated(String keyword, int page) {

        int size = 15; // match your default pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());

        if (keyword == null || keyword.trim().isEmpty()) {
            return projectRepo.findAllByOrderByCreationDateDesc(pageable);
        }

        String lowerKeyword = keyword.trim();

        return projectRepo.searchProjectsPaginated(lowerKeyword, pageable);
    }

}
