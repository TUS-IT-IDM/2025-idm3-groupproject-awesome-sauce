package idm3.project.gallery.service;

import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.repository.ShowcaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ShowcaseService {

    @Autowired
    private ShowcaseRepository showcaseRepository;

    // Save original images here (NOT the thumbnail folder)
    private static final String UPLOAD_DIR = "src/main/resources/static/assets/images/showcases/";

    public List<Showcase> findAll() {
        return (List<Showcase>) showcaseRepository.findAll();
    }

    // Return only live showcases
    public List<Showcase> findAllLive() {
        return showcaseRepository.findByStatus("LIVE");
    }

    // Create (or update) a showcase (no file handling)
    public Showcase save(Showcase showcase) {
        return showcaseRepository.save(showcase);
    }

    // Find by id
    public Showcase findById(long id) {
        return showcaseRepository.findById(id).orElse(null);
    }

    // Delete by id
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

    public long totalShowcases() { return showcaseRepository.count(); }
    public long liveShowcases()  { return showcaseRepository.countByStatus("LIVE"); }
    public long draftShowcases() { return showcaseRepository.countByStatus("PENDING"); } // or PENDING if you use that
    public List<Showcase> recentShowcases() { return showcaseRepository.findTop5ByOrderByShowcaseIdDesc(); }

    public Showcase findById(Long showcaseId) {
        return showcaseRepository.findById(showcaseId).orElse(null);
    }

    public List<Showcase> searchShowcases(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(); // return all showcases if empty
        }
        return showcaseRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }


}
