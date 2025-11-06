package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SavedProjectRepository extends CrudRepository<SavedProject, Long> {

    // Exists check for (employer, project) pair
    boolean existsByEmployerAndProject(User employer, Project project);

    // Fetch a single saved entry for update/read
    Optional<SavedProject> findByEmployerAndProject(User employer, Project project);

    // IDs-only (handy for disabling "Save" buttons)
    @Query("select sp.project.projectId from SavedProject sp where sp.employer = :employer")
    List<Long> findProjectIdsByEmployer(User employer);

    // Full objects (with joined project) for dashboard
    @Query("""
           select sp
           from SavedProject sp
           join fetch sp.project p
           where sp.employer = :employer
           order by sp.id desc
           """)
    List<SavedProject> findAllByEmployerOrderByIdDesc(User employer);

    // Update note in-place
    @Modifying
    @Transactional
    @Query("""
           update SavedProject sp
           set sp.note = :note
           where sp.employer = :employer and sp.project = :project
           """)
    int updateNote(User employer, Project project, String note);

    // Unsave (delete) a project for this employer
    @Transactional
    void deleteByEmployerAndProject(User employer, Project project);

    @Query("SELECT sp FROM SavedProject sp WHERE sp.employer = :employer AND sp.project.projectId = :projectId")
    SavedProject findByEmployerAndProjectId(User employer, Long projectId);

}
