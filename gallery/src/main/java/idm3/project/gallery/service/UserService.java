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

    // Save uploads outside the JAR
    private static final String PROFILE_PICTURE_DIR =
            System.getProperty("user.dir") + "/uploads/profile/";

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

    public boolean registerUser(User user) {
        if (userRepository.existsByUserName(user.getUserName()) ||
                userRepository.existsByEmailAddress(user.getEmailAddress())) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository
                .findByFirstNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailAddressContainingIgnoreCaseOrOrganizationContainingIgnoreCase(
                        keyword, keyword, keyword, keyword
                );
    }

    // ==========================================================
    // 👤 Profile Picture Upload (fixed)
    // ==========================================================
    public void uploadProfilePicture(User user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            System.out.println("No file uploaded or file is empty.");
            return;
        }

        // Ensure folder exists
        Files.createDirectories(Paths.get(PROFILE_PICTURE_DIR));

        // Unique filename
        String filename = user.getUserId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(PROFILE_PICTURE_DIR, filename);

        // Log actual path for debugging
        System.out.println("✅ Saving profile picture to: " + path.toAbsolutePath());

        // Write file to disk
        Files.write(path, file.getBytes());

        // Save relative filename in DB
        user.setProfilePicture(filename);
        userRepository.save(user);
    }

    public User refreshUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
