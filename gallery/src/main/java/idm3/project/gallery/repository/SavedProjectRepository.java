package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedProjectRepository extends JpaRepository<SavedProject, Long> {

    // 🔹 Check if project already saved
    boolean existsByEmployerAndProject(User employer, Project project);

    // 🔹 Find specific saved project
    Optional<SavedProject> findByEmployerAndProject(User employer, Project project);

    // 🔹 Only get saved project IDs
    @Query("""
           select sp.project.projectId 
           from SavedProject sp 
           where sp.employer = :employer
           """)
    List<Long> findProjectIdsByEmployer(User employer);

    // 🔹 All saved projects (fetch join)
    @Query("""
           select sp
           from SavedProject sp
           join fetch sp.project p
           where sp.employer = :employer
           order by sp.id desc
           """)
    List<SavedProject> findAllByEmployerOrderByIdDesc(User employer);

    // 🔹 Update note
    @Modifying
    @Transactional
    @Query("""
           update SavedProject sp
           set sp.note = :note
           where sp.employer = :employer 
             and sp.project = :project
           """)
    int updateNote(User employer, Project project, String note);

    // 🔹 Delete a saved project
    @Transactional
    void deleteByEmployerAndProject(User employer, Project project);

    // 🔹 Find by employer + projectId
    @Query("""
           select sp 
           from SavedProject sp 
           join fetch sp.project 
           where sp.employer = :employer 
             and sp.project.projectId = :projectId
           """)
    SavedProject findByEmployerAndProjectId(User employer, Long projectId);

    // 🔹 Fetch raw saved-project list (used in search)
    List<SavedProject> findByEmployer(User employer);


    /* ============================================================
       PAGINATION METHODS
       ============================================================ */

    // 🔹 Paginated "all saved" list (NO fetch join — required for paging)
    Page<SavedProject> findAllByEmployerOrderByIdDesc(User employer, Pageable pageable);

    // 🔹 Paginated search inside saved projects
    @Query("""
           select sp
           from SavedProject sp
           where sp.employer.userId = :employerId
             and (
                   lower(sp.project.projectName) like :keyword
                or lower(sp.project.projectDescription) like :keyword
                or lower(sp.project.category) like :keyword
             )
           order by sp.id desc
           """)
    Page<SavedProject> searchSavedProjectsForEmployer(@Param("employerId") Long employerId,
                                                      @Param("keyword") String keyword,
                                                      Pageable pageable);
}
