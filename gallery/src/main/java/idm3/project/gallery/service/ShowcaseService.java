package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.repository.ProjectRepository;
import idm3.project.gallery.repository.ShowcaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ShowcaseService {




    @Autowired
    private ShowcaseRepository showcaseRepository;
    public List<Showcase> findAll() {
        return (List<Showcase>) showcaseRepository.findAll();
    }
    private ProjectRepository projectRepository;

    // Return only live showcases
    public List<Showcase> findAllLive() {
        return showcaseRepository.findByStatus("LIVE");
    }

    @Transactional
    public void addProjectToShowcase(Long projectId, Long showcaseId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Showcase showcase = showcaseRepository.findById(showcaseId)
                .orElseThrow(() -> new RuntimeException("Showcase not found"));

        if (!showcase.getProjects().contains(project)) {
            showcase.getProjects().add(project);
        }

        showcaseRepository.save(showcase);
    }}




