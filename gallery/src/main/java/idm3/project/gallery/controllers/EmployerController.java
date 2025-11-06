// src/main/java/idm3/project/gallery/controllers/EmployerController.java
package idm3.project.gallery.controllers;

import idm3.project.gallery.model.User;
import idm3.project.gallery.service.SavedProjectService;
import idm3.project.gallery.service.SavedProjectService.SaveResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/MainGallery") // ✅ top-level MainGallery to support /MainGallery/employerDashboard
public class EmployerController {

    @Autowired
    private SavedProjectService savedProjectService;

    /**
     * Handles POST from "Save to List" button (with optional note).
     * Redirects to HomePage with a success popup.
     */
    @PostMapping("/employer/saveProject")
    public String saveProject(@RequestParam("projectId") Long projectId,
                              @RequestParam(value = "note", required = false) String note,
                              HttpSession session,
                              RedirectAttributes ra) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            ra.addFlashAttribute("error", "Please log in as an employer to save projects.");
            return "redirect:/MainGallery/Login";
        }

        SaveResult result = savedProjectService.saveProjectForEmployer(user, projectId, note);

        switch (result) {
            case SAVED -> ra.addFlashAttribute("message", "✅ Project saved successfully!");
            case ALREADY_SAVED -> ra.addFlashAttribute("message", "⚠️ Project already saved.");
            case PROJECT_NOT_FOUND -> ra.addFlashAttribute("error", "❌ Project not found.");
        }

        // ✅ redirect back to homepage (where success message appears)
        return "redirect:/MainGallery/HomePage";
    }

    /**
     * Employer Dashboard (reachable via /MainGallery/employerDashboard)
     * Displays saved projects with notes.
     */
    @GetMapping("/employerDashboard")
    public String employerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/MainGallery/Login";
        }

        model.addAttribute("saved", savedProjectService.getSavedProjects(user));
        model.addAttribute("user", user);

        // ✅ template location: src/main/resources/templates/employerDashboard.html
        return "employerDashboard";
    }

    @PostMapping("/employer/updateNote")
    public String updateNote(@RequestParam("projectId") Long projectId,
                             @RequestParam("note") String note,
                             HttpSession session,
                             RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            ra.addFlashAttribute("error", "Please log in as an employer to update notes.");
            return "redirect:/MainGallery/Login";
        }

        savedProjectService.updateNoteForSavedProject(user, projectId, note);
        ra.addFlashAttribute("message", "✅ Note updated.");
        return "redirect:/MainGallery/employerDashboard";
    }

    // 🗑️ Delete a saved project (does NOT delete the project itself)
    @PostMapping("/employer/deleteSaved")
    public String deleteSavedProject(@RequestParam("projectId") Long projectId,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            ra.addFlashAttribute("error", "Please log in as an employer to delete saved projects.");
            return "redirect:/MainGallery/Login";
        }

        boolean deleted = savedProjectService.deleteSavedProject(user, projectId);
        if (deleted) {
            ra.addFlashAttribute("message", "🗑️ Project removed from your saved list.");
        } else {
            ra.addFlashAttribute("error", "⚠️ Could not find that saved project.");
        }

        return "redirect:/MainGallery/employerDashboard";
    }

}
