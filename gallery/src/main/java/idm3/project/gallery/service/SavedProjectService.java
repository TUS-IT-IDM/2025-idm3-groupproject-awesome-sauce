package idm3.project.gallery.service;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import idm3.project.gallery.repository.ProjectRepository;
import idm3.project.gallery.repository.SavedProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public void updateNoteForSavedProject(User employer, Long projectId, String newNote) {
        if (employer == null || projectId == null) return;

        Project project = projectRepo.findById(projectId).orElse(null);
        if (project == null) return;

        String cleanNote = (newNote == null) ? "" : newNote.trim();
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

    // ✅ New helper to fetch all projects (used by search)
    public List<Project> getSavedProjectsForEmployer(User employer) {
        List<SavedProject> savedList = savedRepo.findByEmployer(employer);
        return savedList.stream()
                .map(SavedProject::getProject)
                .collect(Collectors.toList());
    }

    // ✅ Keyword search through saved projects
    public List<Project> searchSavedProjects(User employer, String keyword) {
        if (employer == null) return List.of();

        List<Project> savedProjects = getSavedProjectsForEmployer(employer);

        if (keyword == null || keyword.trim().isEmpty()) {
            return savedProjects;
        }

        String lower = keyword.toLowerCase();
        return savedProjects.stream()
                .filter(p -> (p.getProjectName() != null && p.getProjectName().toLowerCase().contains(lower))
                        || (p.getProjectDescription() != null && p.getProjectDescription().toLowerCase().contains(lower))
                        || (p.getCategory() != null && p.getCategory().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public SavedProject findByEmployerAndProjectId(User employer, Long projectId) {
        if (employer == null || projectId == null) return null;
        return savedRepo.findByEmployerAndProjectId(employer, projectId);
    }

}
