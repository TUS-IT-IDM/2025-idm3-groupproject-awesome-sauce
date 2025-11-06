package idm3.project.gallery.service;

import idm3.project.gallery.model.User;
import idm3.project.gallery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticate user by email and password.
     * Returns the User if both match, otherwise null.
     */
    public User authenticate(String email, String password) {
        return userRepository.findByEmailAddressAndPassword(email, password);
    }

    /**
     * Check if an email exists in the database.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAddress(email);
    }

    /**
     * Find a user by email.
     */
    public User findByEmail(String email) {
        return userRepository.findByEmailAddress(email);
    }

    /**
     * Compare given password with the stored one.
     * (If you add password hashing later, update this to use BCrypt matches.)
     */
    public boolean passwordMatches(User user, String rawPassword) {
        return user != null && user.getPassword().equals(rawPassword);
    }

    /**
     * Register a new user (checks both username and email for duplicates).
     */
    public boolean registerUser(User user) {
        if (userRepository.existsByUserName(user.getUserName()) ||
                userRepository.existsByEmailAddress(user.getEmailAddress())) {
            return false; // User already exists
        }
        userRepository.save(user);
        return true;
    }

    // Search users by name, email, or organization
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll();
        }

        return userRepository.findByFirstNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrOrganizationContainingIgnoreCase(
                keyword, keyword, keyword, keyword
        );
    }


}
