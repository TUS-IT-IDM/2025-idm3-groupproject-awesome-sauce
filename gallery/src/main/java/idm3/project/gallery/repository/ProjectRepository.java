package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    long count();

    List<Project> findAllByOrderByCreationDateDesc();

    List<Project> findTop5ByOrderByCreationDateDesc();

    // 🔹 Non-paginated search (still used in some places)
    List<Project> findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
            String name, String description
    );

    // 🔹 Paginated full list
    Page<Project> findAllByOrderByCreationDateDesc(Pageable pageable);

    // 🔹 Paginated search (name + description)
    Page<Project> findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable
    );

    // 🔹 Paginated search by keyword (used by ProjectService.searchProjectsPaginated)
    @Query("""
           select p
           from Project p
           where lower(p.projectName) like lower(concat('%', :keyword, '%'))
              or lower(p.projectDescription) like lower(concat('%', :keyword, '%'))
           order by p.creationDate desc
           """)
    Page<Project> searchProjectsPaginated(@Param("keyword") String keyword,
                                          Pageable pageable);

    // 🔹 Paginated filter by category only
    Page<Project> findByCategoryIgnoreCase(String category, Pageable pageable);

    // 🔹 Paginated search by keyword + category (used by filtering on homepage)
    @Query("""
           select p
           from Project p
           where (:keyword is null
                  or lower(p.projectName) like lower(concat('%', :keyword, '%'))
                  or lower(p.projectDescription) like lower(concat('%', :keyword, '%')))
             and (:category is null
                  or lower(p.category) = lower(:category))
           order by p.creationDate desc
           """)
    Page<Project> searchByKeywordAndCategory(@Param("keyword") String keyword,
                                             @Param("category") String category,
                                             Pageable pageable);
}
