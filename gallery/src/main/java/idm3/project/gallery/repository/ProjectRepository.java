package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findByProjectNameContainingIgnoreCaseOrProjectDescriptionContainingIgnoreCase(
            String name, String description);

    // Find all projects ordered by creation date descending
    long count();
    List<Project> findAllByOrderByCreationDateDesc();
    List<Project> findTop5ByOrderByCreationDateDesc();






}



