package idm3.project.gallery.service;

import idm3.project.gallery.model.User;
import idm3.project.gallery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Save profile uploads outside the JAR
    private static final String PROFILE_PICTURE_DIR =
            System.getProperty("user.dir") + "/uploads/profile/";

    // =========================================
    // AUTHENTICATION
    // =========================================

    public User authenticate(String email, String password) {
        return userRepository.findByEmailAddressAndPassword(email, password);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAddress(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailAddress(email);
    }

    public boolean passwordMatches(User user, String rawPassword) {
        return user != null && user.getPassword().equals(rawPassword);
    }

    // =========================================
    // REGISTRATION
    // =========================================

    public boolean registerUser(User user) {
        if (userRepository.existsByUserName(user.getUserName()) ||
                userRepository.existsByEmailAddress(user.getEmailAddress())) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    // =========================================
    // SEARCH
    // =========================================

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll();
        }

        return userRepository
                .findByFirstNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrOrganizationContainingIgnoreCase(
                        keyword, keyword, keyword, keyword
                );
    }

    // =========================================
    // PROFILE UPLOAD (FIXED)
    // =========================================

    public void uploadProfilePicture(User user, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            System.out.println("No profile picture uploaded.");
            return;
        }

        // Ensure upload folder exists
        Files.createDirectories(Paths.get(PROFILE_PICTURE_DIR));

        // Unique filename: userID_timestamp_originalName
        String filename = user.getUserId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(PROFILE_PICTURE_DIR, filename);

        // Save file
        Files.write(path, file.getBytes());

        // Store filename in DB
        user.setProfilePicture(filename);
        userRepository.save(user);
    }

    // =========================================
    // REFRESH (fetch updated user)
    // =========================================

    public User refreshUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
