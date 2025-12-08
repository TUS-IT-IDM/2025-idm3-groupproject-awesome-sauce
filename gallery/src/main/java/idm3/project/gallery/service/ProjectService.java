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

    public List<Project> searchProjects(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return (List<Project>) projectRepo.findAll();

        }
        return projectRepo.findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
                keyword, keyword);
    }





}
