package idm3.project.gallery.controllers;

import idm3.project.gallery.model.User;
import idm3.project.gallery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/MainGallery/profile")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Display the logged-in user's profile page.
     */
    @GetMapping
    public String userProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/MainGallery/Login";
        }

        // Always fetch the most up-to-date user info from the DB
        User refreshedUser = userService.refreshUser(user.getUserId());
        session.setAttribute("loggedInUser", refreshedUser);
        model.addAttribute("user", refreshedUser);

        return "profile"; // points to profile.html
    }

    /**
     * Handle profile picture upload + instant refresh.
     */
    @PostMapping("/uploadPicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
                                       HttpSession session,
                                       RedirectAttributes ra) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            ra.addFlashAttribute("error", "⚠️ You must be logged in to upload a profile picture.");
            return "redirect:/MainGallery/Login";
        }

        try {
            // Save the uploaded file
            userService.uploadProfilePicture(user, file);

            // ✅ Reload user data so new image shows immediately
            User updatedUser = userService.refreshUser(user.getUserId());
            session.setAttribute("loggedInUser", updatedUser);

            ra.addFlashAttribute("message", "✅ Profile picture updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "❌ Failed to upload picture: " + e.getMessage());
        }

        // ✅ Redirect ensures immediate refresh of the displayed image
        return "redirect:/MainGallery/profile";
    }
}
