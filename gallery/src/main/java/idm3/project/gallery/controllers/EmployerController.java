package idm3.project.gallery.controllers;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import idm3.project.gallery.service.SavedProjectService;
import idm3.project.gallery.service.SavedProjectService.SaveResult;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/MainGallery") // ✅ back to original structure
public class EmployerController {

    @Autowired
    private SavedProjectService savedProjectService;


    // ------------------------------------------------------------
    // SAVE PROJECT
    // ------------------------------------------------------------
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

        return "redirect:/MainGallery/HomePage";
    }


    // ------------------------------------------------------------
    // EMPLOYER DASHBOARD (PAGINATED + SEARCH + SORT)
    // ------------------------------------------------------------
    @GetMapping("/employerDashboard")
    public String employerDashboard(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String sortBy,
                                    HttpSession session,
                                    Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/MainGallery/Login";
        }

        Page<SavedProject> savedPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 🔎 Search + sort
            savedPage = savedProjectService.searchSavedPaginated(keyword, user, page, sortBy);
        } else {
            // 📄 Just paginated list + sort
            savedPage = savedProjectService.getSavedProjectsPaginated(user, page, sortBy);
        }

        model.addAttribute("savedPage", savedPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);  // so the UI can keep the selected option
        model.addAttribute("user", user);

        return "employerDashboard";
    }


    // ------------------------------------------------------------
    // UPDATE NOTE
    // ------------------------------------------------------------
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


    // ------------------------------------------------------------
    // DELETE SINGLE SAVED PROJECT
    // ------------------------------------------------------------
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


    // ------------------------------------------------------------
    //  BULK DELETE SAVED PROJECTS
    // ------------------------------------------------------------
    @PostMapping("/employer/bulkDeleteSaved")
    public String bulkDeleteSavedProjects(
            @RequestParam(name = "selectedProjectIds", required = false) List<Long> selectedProjectIds,
            HttpSession session,
            RedirectAttributes ra) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            ra.addFlashAttribute("error", "Please log in as an employer to delete saved projects.");
            return "redirect:/MainGallery/Login";
        }

        if (selectedProjectIds == null || selectedProjectIds.isEmpty()) {
            ra.addFlashAttribute("error", "No projects selected.");
            return "redirect:/MainGallery/employerDashboard";
        }

        int deletedCount = 0;
        for (Long projectId : selectedProjectIds) {
            if (savedProjectService.deleteSavedProject(user, projectId)) {
                deletedCount++;
            }
        }

        if (deletedCount > 0) {
            ra.addFlashAttribute("message",
                    "🗑️ " + deletedCount + " project(s) removed from your saved list.");
        } else {
            ra.addFlashAttribute("error", "⚠️ No matching saved projects were found to delete.");
        }

        return "redirect:/MainGallery/employerDashboard";
    }


    // ------------------------------------------------------------
    // FULL VIEW PAGE
    // ------------------------------------------------------------
    @GetMapping("/employer/saved/{projectId}")
    public ModelAndView viewSavedProject(@PathVariable Long projectId, HttpSession session) {

        User employer = (User) session.getAttribute("loggedInUser");
        if (employer == null) {
            return new ModelAndView("redirect:/MainGallery/Login");
        }

        SavedProject saved = savedProjectService.findByEmployerAndProjectId(employer, projectId);
        if (saved == null) {
            return new ModelAndView("redirect:/MainGallery/employerDashboard");
        }

        ModelAndView mav = new ModelAndView("employer/employerSavedDetails");
        mav.addObject("saved", saved);

        return mav;
    }


    // ------------------------------------------------------------
    // JSON API FOR MODAL
    // ------------------------------------------------------------
    @GetMapping("/employer/saved/{projectId}/json")
    @ResponseBody
    public SavedProject getSavedProjectJson(@PathVariable Long projectId, HttpSession session) {

        User employer = (User) session.getAttribute("loggedInUser");
        if (employer == null) return null;

        return savedProjectService.findByEmployerAndProjectId(employer, projectId);
    }

}
