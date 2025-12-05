package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    long count();

    List<Project> findAllByOrderByCreationDateDesc();
    List<Project> findTop5ByOrderByCreationDateDesc();

    // Non-paginated search (keep if still used)
    List<Project> findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
            String name, String description);

    // Pagination for full list
    Page<Project> findAllByOrderByCreationDateDesc(Pageable pageable);

    // Pagination search for name + description (USE THIS going forward)
    Page<Project> findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    @Query("""
    select p from Project p
    where lower(p.projectName) like lower(concat('%', :keyword, '%'))
       or lower(p.projectDescription) like lower(concat('%', :keyword, '%'))
    order by p.creationDate desc
""")
    Page<Project> searchProjectsPaginated(String keyword, Pageable pageable);


}






