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
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Default profile picture directory (single shared folder)
//     private static final String PROFILE_PICTURE_DIR = "src/main/resources/static/assets/images/profile/";
//     switch to the above statement if the current file directory does not work
    /**
     * Authenticate user by email and password.
     */
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

    /**
     * Compare given password with stored one.
     */
    public boolean passwordMatches(User user, String rawPassword) {
        return user != null && user.getPassword().equals(rawPassword);
    }

    /**
     * Register a new user (ensuring no duplicates).
     */
    public boolean registerUser(User user) {
        if (userRepository.existsByUserName(user.getUserName()) ||
                userRepository.existsByEmailAddress(user.getEmailAddress())) {
            return false;
        }
        userRepository.save(user);
        return true;
    }

    /**
     * Search users by name, email, or organization.
     */
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


        if (file != null && !file.isEmpty()) {
            // Ensure folder exists
            Files.createDirectories(Paths.get(PROFILE_PICTURE_DIR));

            // Name file uniquely using user ID
            filename = user.getUserId() + "_" + file.getOriginalFilename();
            path = Paths.get(PROFILE_PICTURE_DIR, filename);

            // Save file to static folder
            Files.write(path, file.getBytes());

            // Store filename (relative to profile/ directory)
            user.setProfilePicture(filename);
            userRepository.save(user);
        }
    }


    public User refreshUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // ==========================================================
    // 👑 Senior Admin – Admin management helpers
    // ==========================================================

    /**
     * Find a user by id (used by SeniorAdminUserController).
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get all users whose role is admin or senior admin.
     */
    public List<User> findAllAdmins() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u != null && isAdminRole(u.getUserType()))
                .collect(Collectors.toList());
    }

    /**
     * Search only admins/senior admins by keyword (name/email/org).
     * Reuses the existing searchUsers(...) method.
     */
    public List<User> findAdmins(String keyword) {
        List<User> users = searchUsers(keyword);
        return users.stream()
                .filter(u -> u != null && isAdminRole(u.getUserType()))
                .collect(Collectors.toList());
    }

    /**
     * Create a new admin (or senior admin) account.
     * If an invalid role is provided, default to "admin".
     */
    public User createAdmin(User admin) {
        String role = admin.getUserType();
        if (!isAdminRole(role)) {
            role = "admin";
        }
        admin.setUserType(role.toLowerCase());
        return userRepository.save(admin);
    }

    /**
     * Update an existing admin (name, email, org, username, role).
     */
    public User updateAdmin(Long id, User form) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(form.getFirstName());
                    existing.setSurname(form.getSurname());
                    existing.setEmailAddress(form.getEmailAddress());
                    existing.setOrganization(form.getOrganization());
                    existing.setUserName(form.getUserName());

                    if (isAdminRole(form.getUserType())) {
                        existing.setUserType(form.getUserType().toLowerCase());
                    }
                    return userRepository.save(existing);
                })
                .orElse(null);
    }

    /**
     * Delete an admin or senior admin account.
     */
    public void deleteAdmin(Long id) {
        userRepository.findById(id).ifPresent(u -> {
            if (isAdminRole(u.getUserType())) {
                userRepository.delete(u);
            }
        });
    }

    /**
     * Internal check to see if a role is admin-type.
     */
    private boolean isAdminRole(String role) {
        if (role == null) return false;
        String r = role.trim().toLowerCase();
        return "admin".equals(r) || "senioradmin".equals(r);
    }


}
