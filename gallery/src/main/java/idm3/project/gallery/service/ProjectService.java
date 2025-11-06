package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepo;

    /**
     * Fetch all projects ordered by creation date (newest first)
     */
    public List<Project> findAllOrderedByCreationDate() {
        return projectRepo.findAllByOrderByCreationDateDesc();
    }

    /**
     * Return all projects (unordered)
     */
    public List<Project> findAll() {
        return (List<Project>) projectRepo.findAll();
    }

    /**
     * Count all projects (for dashboard KPIs)
     */
    public long totalProjects() {
        return projectRepo.count();
    }

    /**
     * Fetch the 5 most recent projects
     */
    public List<Project> recentProjects() {
        return projectRepo.findTop5ByOrderByCreationDateDesc();
    }

    /**
     * Find a project by its ID
     */
    public Project findById(long id) {
        return projectRepo.findById(id).orElse(null);
    }

    /**
     * Save or update a project
     */
    public Project save(Project project) {
        return projectRepo.save(project);
    }

    /**
     * Delete a project by ID
     */
    public void deleteById(long id) {
        projectRepo.deleteById(id);
    }
}
