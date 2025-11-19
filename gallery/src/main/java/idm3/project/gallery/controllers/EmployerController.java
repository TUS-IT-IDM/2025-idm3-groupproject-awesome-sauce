package idm3.project.gallery.controllers;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.SavedProject;
import idm3.project.gallery.model.User;
import idm3.project.gallery.service.SavedProjectService;
import idm3.project.gallery.service.SavedProjectService.SaveResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/MainGallery") // ✅ back to original structure
public class EmployerController {

    @Autowired
    private SavedProjectService savedProjectService;

    // ✅ Save project
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

    // ✅ Employer dashboard
    @GetMapping("/employerDashboard")
    public String employerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"employer".equalsIgnoreCase(user.getUserType())) {
            return "redirect:/MainGallery/Login";
        }

        model.addAttribute("saved", savedProjectService.getSavedProjects(user));
        model.addAttribute("user", user);

        // ✅ template location
        return "employerDashboard";
    }

    // ✅ Update note
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

    // ✅ Delete saved project
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

    // ✅ Search saved projects
    @GetMapping("/searchSaved")
    public ModelAndView searchSaved(@RequestParam(value = "keyword", required = false) String keyword,
                                    HttpSession session) {
        User employer = (User) session.getAttribute("loggedInUser");
        if (employer == null) {
            return new ModelAndView("redirect:/MainGallery/Login");
        }

        List<Project> filtered = savedProjectService.searchSavedProjects(employer, keyword);

        List<SavedProject> results = filtered.stream()
                .map(p -> {
                    SavedProject sp = new SavedProject();
                    sp.setProject(p);
                    sp.setEmployer(employer);
                    return sp;
                })
                .collect(Collectors.toList());

        ModelAndView mav = new ModelAndView("employerDashboard");
        mav.addObject("saved", results);
        mav.addObject("keyword", keyword);
        return mav;
    }

    // ✅ Drill-down page
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

    // ✅ API used by the JS modal to fetch saved project details
    @GetMapping("/employer/saved/{projectId}/json")
    @ResponseBody
    public SavedProject getSavedProjectJson(@PathVariable Long projectId, HttpSession session) {
        User employer = (User) session.getAttribute("loggedInUser");
        if (employer == null) return null;

        return savedProjectService.findByEmployerAndProjectId(employer, projectId);
    }

}
