package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedProjectRepository extends JpaRepository<SavedProject, Long> {

    // 🔹 Check if a project is already saved by this employer
    boolean existsByEmployerAndProject(User employer, Project project);

    // 🔹 Find a specific saved project
    Optional<SavedProject> findByEmployerAndProject(User employer, Project project);

    // 🔹 Fetch only project IDs (used to disable "Save" button in UI)
    @Query("""
           select sp.project.projectId 
           from SavedProject sp 
           where sp.employer = :employer
           """)
    List<Long> findProjectIdsByEmployer(User employer);

    // 🔹 Fetch all saved projects with joined Project data
    @Query("""
           select sp
           from SavedProject sp
           join fetch sp.project p
           where sp.employer = :employer
           order by sp.id desc
           """)
    List<SavedProject> findAllByEmployerOrderByIdDesc(User employer);

    // 🔹 Update note for a saved project (inline update)
    @Modifying
    @Transactional
    @Query("""
           update SavedProject sp
           set sp.note = :note
           where sp.employer = :employer 
             and sp.project = :project
           """)
    int updateNote(User employer, Project project, String note);

    // 🔹 Delete a project from employer’s saved list (not the project itself)
    @Transactional
    void deleteByEmployerAndProject(User employer, Project project);

    // 🔹 Find by employer and projectId (for drill-down or quick access)
    @Query("""
           select sp 
           from SavedProject sp 
           join fetch sp.project 
           where sp.employer = :employer 
             and sp.project.projectId = :projectId
           """)
    SavedProject findByEmployerAndProjectId(User employer, Long projectId);

    // 🔹 Fetch all SavedProject entries for one employer (used in search)
    List<SavedProject> findByEmployer(User employer);
}
