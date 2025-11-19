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

        // Refresh from DB in case their info changed
        User refreshedUser = userService.refreshUser(user.getUserId());
        session.setAttribute("loggedInUser", refreshedUser);
        model.addAttribute("user", refreshedUser);

        return "profile"; // points to profile.html
    }

    /**
     * Handle profile picture uploads.
     */
    @PostMapping("/uploadPicture")
    public String uploadProfilePicture(@RequestParam("profilePicture") MultipartFile file,
                                       HttpSession session,
                                       Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/MainGallery/Login";
        }

        try {
            userService.uploadProfilePicture(user, file);

            // Refresh and update session
            User updatedUser = userService.refreshUser(user.getUserId());
            session.setAttribute("loggedInUser", updatedUser);
            model.addAttribute("user", updatedUser);
            model.addAttribute("success", "Profile picture updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error uploading file: " + e.getMessage());
        }

        System.out.println("Upload endpoint triggered successfully!");
        return "profile";
    }


}
