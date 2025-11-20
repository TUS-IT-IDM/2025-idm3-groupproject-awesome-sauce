package idm3.project.gallery.repository;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.Showcase;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ShowcaseRepository extends CrudRepository<Showcase, Long> {

    List<Showcase> findByStatus(String status);

    // stats + recent
    long count();                                   // inherited, but explicit use is fine
    long countByStatus(String status);
    List<Showcase> findTop5ByOrderByShowcaseIdDesc();

    List<Showcase> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);


}






