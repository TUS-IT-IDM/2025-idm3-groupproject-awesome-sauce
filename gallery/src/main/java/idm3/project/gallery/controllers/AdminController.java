package idm3.project.gallery.controllers;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.model.User;
import idm3.project.gallery.service.ProjectService;
import idm3.project.gallery.service.ShowcaseService;
import idm3.project.gallery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/MainGallery/admin/showcases")
public class AdminController {

    private final ShowcaseService showcaseService;
    private final UserService userService;
    private final ProjectService projectService;

    @Autowired
    public AdminController(ShowcaseService showcaseService, UserService userService, ProjectService projectService) {
        this.showcaseService = showcaseService;
        this.userService = userService;
        this.projectService = projectService;
    }

    // ===============================================================
    // 🟢 CREATE
    // ===============================================================
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("showcase", new Showcase());
        return "admin/showcaseForm";
    }

    @PostMapping("/create")
    public String createSubmit(@ModelAttribute("showcase") Showcase showcase,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               @SessionAttribute("loggedInUser") User loggedInUser) {
        try {
            if (showcase.getStatus() != null) {
                showcase.setStatus(showcase.getStatus().trim().toUpperCase());
            }

            // link to logged-in user
            showcase.setCreatedBy(loggedInUser);

            // save showcase
            showcaseService.saveShowcaseWithImage(showcase, imageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // redirect back to main dashboard
        return "redirect:/MainGallery/adminDashboard";
    }

    // ===============================================================
    // 🟡 EDIT + DELETE
    // ===============================================================
    @GetMapping("/edit/{id}")
    public String editShowcaseForm(@PathVariable("id") Long id, Model model) {
        Showcase showcase = showcaseService.findById(id);
        if (showcase == null) {
            return "redirect:/MainGallery/adminDashboard";
        }
        model.addAttribute("showcase", showcase);
        return "admin/showcaseEdit";
    }

    @PostMapping("/edit/{id}")
    public String updateShowcase(@PathVariable("id") Long id,
                                 @ModelAttribute("showcase") Showcase updatedShowcase,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Showcase existing = showcaseService.findById(id);
        if (existing == null) {
            return "redirect:/MainGallery/adminDashboard";
        }

        try {
            existing.setName(updatedShowcase.getName());
            existing.setDescription(updatedShowcase.getDescription());
            existing.setStatus(updatedShowcase.getStatus().trim().toUpperCase());

            if (imageFile != null && !imageFile.isEmpty()) {
                showcaseService.saveShowcaseWithImage(existing, imageFile);
            } else {
                showcaseService.save(existing);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/MainGallery/adminDashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteShowcase(@PathVariable("id") Long id) {
        showcaseService.deleteById(id);
        return "redirect:/MainGallery/adminDashboard";
    }

    // ===============================================================
    // 🔍 SEARCH (Showcases or Users)
    // ===============================================================
    @GetMapping("/searchUsers")
    public ModelAndView searchUsers(@RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView mav = new ModelAndView("adminDashboard");
        List<User> users = userService.searchUsers(keyword);
        mav.addObject("users", users);
        mav.addObject("keyword", keyword);

        // dashboard stats
        populateDashboardStats(mav);
        return mav;
    }

    @GetMapping("/searchShowcases")
    public ModelAndView searchShowcases(@RequestParam(value = "keyword", required = false) String keyword) {
        ModelAndView mav = new ModelAndView("adminDashboard");
        List<Showcase> showcases = showcaseService.searchShowcases(keyword);
        mav.addObject("searchResults", showcases);
        mav.addObject("keyword", keyword);

        // dashboard stats
        populateDashboardStats(mav);
        return mav;
    }

    // ===============================================================
    // 🧩 API — Used by JS modal for project drill-down
    // ===============================================================
    @GetMapping("/{id}/projects")
    @ResponseBody
    public List<Project> getProjectsForShowcase(@PathVariable("id") Long id) {
        List<Project> projects = showcaseService.getProjectsForShowcase(id);
        // Prevent recursive user data loops
        projects.forEach(p -> p.setUserID(null));
        return projects;
    }

    // ===============================================================
    // 🟣 VIEW DETAILS (HTML fallback if needed)
    // ===============================================================
    @GetMapping("/{id}")
    public String viewShowcaseDetails(@PathVariable("id") Long id, Model model) {
        Showcase showcase = showcaseService.findById(id);
        if (showcase == null) {
            return "redirect:/MainGallery/adminDashboard";
        }

        List<Project> projectsInShowcase = showcaseService.getProjectsForShowcase(id);
        model.addAttribute("showcase", showcase);
        model.addAttribute("projects", projectsInShowcase);
        return "admin/showcaseDetails";
    }

    // ===============================================================
    // ♻️ Helper method for dashboard stats
    // ===============================================================
    private void populateDashboardStats(ModelAndView mav) {
        mav.addObject("totalShowcases", showcaseService.totalShowcases());
        mav.addObject("liveShowcases", showcaseService.liveShowcases());
        mav.addObject("draftShowcases", showcaseService.draftShowcases());
        mav.addObject("totalProjects", projectService.totalProjects());
        mav.addObject("recentShowcases", showcaseService.recentShowcases());
        mav.addObject("recentProjects", projectService.recentProjects());
    }

    // ===============================================================
//  REMOVE PROJECT FROM SHOWCASE
// ===============================================================
    @PostMapping("/{showcaseId}/removeProject/{projectId}")
    @ResponseBody
    public String removeProjectFromShowcase(@PathVariable("showcaseId") Long showcaseId,
                                            @PathVariable("projectId") Long projectId) {
        try {
            showcaseService.removeProjectFromShowcase(showcaseId, projectId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


    @PostMapping("/bulk-delete")
    public String bulkDelete(@RequestParam(required = false, name="selectedIds")
                             List<Long> selectedIds,
                             RedirectAttributes redirectAttributes) {

        if (selectedIds == null || selectedIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No showcases selected.");
            return "redirect:/MainGallery/admin/showcases";
        }

        showcaseService.deleteShowcasesByIds(selectedIds);

        redirectAttributes.addFlashAttribute("success",
                selectedIds.size() + " showcase(s) deleted successfully.");

        return "redirect:/MainGallery/admin/showcases";
    }



}
