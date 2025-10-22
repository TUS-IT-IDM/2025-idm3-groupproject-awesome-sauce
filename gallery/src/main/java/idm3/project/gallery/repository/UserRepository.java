package idm3.project.gallery.repository;

import idm3.project.gallery.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    // Used by UserService.authenticate(...)
    User findByEmailAddressAndPassword(String emailAddress, String password);

    // Used by UserService.findByEmail(...)
    User findByEmailAddress(String emailAddress);

    // Used by UserService.registerUser(...)
    boolean existsByUserName(String userName);
    boolean existsByEmailAddress(String emailAddress);

    // (Optional) keep if any old code still uses username+password auth
    User findByUserNameAndPassword(String userName, String password);
}
