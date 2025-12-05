package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import idm3.project.gallery.repository.ProjectRepository;
import idm3.project.gallery.repository.SavedProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedProjectService {

    @Autowired
    private SavedProjectRepository savedRepo;

    @Autowired
    private ProjectRepository projectRepo;

    private static final int PAGE_SIZE = 9;

    public enum SaveResult { SAVED, ALREADY_SAVED, PROJECT_NOT_FOUND }

    // ------------------------------------------------------------
    // SAVE PROJECT
    // ------------------------------------------------------------
    public SaveResult saveProjectForEmployer(User employer, Long projectId, String initialNote) {
        if (employer == null || projectId == null) {
            return SaveResult.PROJECT_NOT_FOUND;
        }

        Project project = projectRepo.findById(projectId).orElse(null);
        if (project == null) {
            return SaveResult.PROJECT_NOT_FOUND;
        }

        if (savedRepo.existsByEmployerAndProject(employer, project)) {
            return SaveResult.ALREADY_SAVED;
        }

        SavedProject entry = new SavedProject();
        entry.setEmployer(employer);
        entry.setProject(project);
        entry.setNote(initialNote == null ? "" : initialNote.trim());

        savedRepo.save(entry);

        return SaveResult.SAVED;
    }

    // ------------------------------------------------------------
    // PAGINATED GETTER
    // ------------------------------------------------------------
    public Page<SavedProject> getSavedProjectsPaginated(User employer, int page) {
        if (employer == null) return Page.empty();

        return savedRepo.findAllByEmployerOrderByIdDesc(
                employer,
                PageRequest.of(page, PAGE_SIZE)
        );
    }

    // ------------------------------------------------------------
    // PAGINATED SEARCH
    // ------------------------------------------------------------
    public Page<SavedProject> searchSavedPaginated(String keyword, User employer, int page) {
        if (employer == null) return Page.empty();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getSavedProjectsPaginated(employer, page);
        }

        String search = "%" + keyword.trim().toLowerCase() + "%";

        return savedRepo.searchSavedProjectsForEmployer(
                employer.getUserId(),
                search,
                PageRequest.of(page, PAGE_SIZE)
        );
    }

    // ------------------------------------------------------------
    // OLD NON-PAGINATED UTILS (still used by modals)
    // ------------------------------------------------------------
    public List<Long> getSavedProjectIds(User employer) {
        if (employer == null) return List.of();
        return savedRepo.findProjectIdsByEmployer(employer);
    }

    public void updateNoteForSavedProject(User employer, Long projectId, String newNote) {
        if (employer == null || projectId == null) return;

        Project project = projectRepo.findById(projectId).orElse(null);
        if (project == null) return;

        String cleanNote = (newNote == null) ? "" : newNote.trim();
        savedRepo.updateNote(employer, project, cleanNote);
    }

    public boolean deleteSavedProject(User employer, Long projectId) {
        SavedProject existing = savedRepo.findByEmployerAndProjectId(employer, projectId);
        if (existing != null) {
            savedRepo.delete(existing);
            return true;
        }
        return false;
    }

    public SavedProject findByEmployerAndProjectId(User employer, Long projectId) {
        if (employer == null || projectId == null) return null;
        return savedRepo.findByEmployerAndProjectId(employer, projectId);
    }
}
