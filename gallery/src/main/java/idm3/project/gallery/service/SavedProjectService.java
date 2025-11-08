// src/main/java/idm3/project/gallery/service/SavedProjectService.java
package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import idm3.project.gallery.repository.ProjectRepository;
import idm3.project.gallery.repository.SavedProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedProjectService {

    @Autowired
    private SavedProjectRepository savedRepo;

    @Autowired
    private ProjectRepository projectRepo;

    public enum SaveResult { SAVED, ALREADY_SAVED, PROJECT_NOT_FOUND }

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

    public List<Long> getSavedProjectIds(User employer) {
        if (employer == null) return List.of();
        return savedRepo.findProjectIdsByEmployer(employer);
    }

    public List<SavedProject> getSavedProjects(User employer) {
        if (employer == null) return List.of();
        return savedRepo.findAllByEmployerOrderByIdDesc(employer);
    }

    // ✅ NEW METHOD — update note for a saved project
    public void updateNoteForSavedProject(User employer, Long projectId, String newNote) {
        if (employer == null || projectId == null) return;

        Project project = projectRepo.findById(projectId).orElse(null);
        if (project == null) return;

        // Trim note to avoid null or whitespace-only entries
        String cleanNote = (newNote == null) ? "" : newNote.trim();

        // Update directly in DB using repository’s query
        savedRepo.updateNote(employer, project, cleanNote);
    }

    public boolean deleteSavedProject(User employer, Long projectId) {
        var existing = savedRepo.findByEmployerAndProjectId(employer, projectId);
        if (existing != null) {
            savedRepo.delete(existing);
            return true;
        }
        return false;
    }

}
