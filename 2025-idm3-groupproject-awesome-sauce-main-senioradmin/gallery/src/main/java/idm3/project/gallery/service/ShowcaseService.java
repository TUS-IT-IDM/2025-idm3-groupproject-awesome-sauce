package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.repository.ShowcaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class ShowcaseService {

    @Autowired
    private ShowcaseRepository showcaseRepository;

    // Directory for uploaded showcase images
    private static final String UPLOAD_DIR = "src/main/resources/static/assets/images/showcases/";


    public List<Showcase> findAll() {
        return (List<Showcase>) showcaseRepository.findAll();
    }

    public List<Showcase> findAllLive() {
        return showcaseRepository.findByStatus("LIVE");
    }

    public Showcase save(Showcase showcase) {
        return showcaseRepository.save(showcase);
    }

    public Showcase findById(long id) {
        return showcaseRepository.findById(id).orElse(null);
    }

    public void deleteById(long id) {
        showcaseRepository.deleteById(id);
    }

    public Showcase saveShowcaseWithImage(Showcase showcase, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            String filename = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            Files.write(filePath, file.getBytes());
            showcase.setImage(filename);
        }
        return showcaseRepository.save(showcase);
    }

    // ================================================================
    // 🔹 STATS & FILTERING
    // ================================================================

    public long totalShowcases() { return showcaseRepository.count(); }

    public long liveShowcases()  { return showcaseRepository.countByStatus("LIVE"); }

    public long draftShowcases() { return showcaseRepository.countByStatus("PENDING"); }

    public List<Showcase> recentShowcases() { return showcaseRepository.findTop5ByOrderByShowcaseIdDesc(); }

    public List<Showcase> searchShowcases(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return showcaseRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    // ================================================================
    // 🔹 PROJECT MANAGEMENT INSIDE SHOWCASE (no separate repo)
    // ================================================================

    /** ✅ Return all projects in a showcase */
    public List<Project> getProjectsForShowcase(Long showcaseId) {
        Showcase showcase = showcaseRepository.findById(showcaseId).orElse(null);
        if (showcase == null || showcase.getProjects() == null) {
            return List.of();
        }
        return showcase.getProjects();
    }

    /** ✅ Remove a project from the showcase (does NOT delete project itself) */
    @Transactional
    public void removeProjectFromShowcase(Long showcaseId, Long projectId) {
        Showcase showcase = showcaseRepository.findById(showcaseId).orElse(null);
        if (showcase != null && showcase.getProjects() != null) {
            showcase.getProjects().removeIf(p -> p.getProjectId() == projectId);
            showcaseRepository.save(showcase);
        }
    }
}
