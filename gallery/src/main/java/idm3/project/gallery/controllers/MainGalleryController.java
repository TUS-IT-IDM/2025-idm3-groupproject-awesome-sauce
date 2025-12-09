package idm3.project.gallery.controllers;

import idm3.project.gallery.model.LoginDTO;
import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.model.User;
import idm3.project.gallery.service.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/MainGallery")
public class MainGalleryController {

    @Autowired private UserService userService;
    @Autowired private ShowcaseService showcaseService;
    @Autowired private ProjectService projectService;
    @Autowired private ThumbnailService thumbnailService;
    @Autowired private SavedProjectService savedProjectService;


    /* ============================================================
       LOGIN
       ============================================================ */

    @GetMapping("/Login")
    public ModelAndView showLoginPage() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("loginDTO", new LoginDTO());
        return mav;
    }

    @PostMapping("/Login")
    public ModelAndView handleLogin(
            @ModelAttribute("loginDTO") @Valid LoginDTO loginDTO,
            BindingResult result,
            HttpSession session) {

        if (result.hasErrors()) {
            return new ModelAndView("login");
        }

        ModelAndView mv = new ModelAndView("login");

        User user = userService.findByEmail(loginDTO.getEmailAddress());
        if (user == null) {
            result.rejectValue("emailAddress", "invalidEmail", "No account found with that email.");
            return mv;
        }

        if (!userService.passwordMatches(user, loginDTO.getPassword())) {
            result.rejectValue("password", "invalidPassword", "Incorrect password.");
            return mv;
        }

        // SUCCESS — store in session
        session.setAttribute("loggedInUser", user);

        return switch (user.getUserType().toLowerCase()) {
            case "admin"      -> new ModelAndView("redirect:/MainGallery/adminDashboard");
            case "student"    -> new ModelAndView("redirect:/MainGallery/studentDashboard");
            case "senioradmin"-> new ModelAndView("redirect:/MainGallery/seniorAdminDashboard");
            case "employer"   -> new ModelAndView("redirect:/MainGallery/employerDashboard");
            default           -> new ModelAndView("redirect:/MainGallery/HomePage");
        };
    }

    @GetMapping("/Logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/MainGallery/Login";
    }


    /* ============================================================
       REGISTRATION
       ============================================================ */

    @GetMapping("/Register")
    public ModelAndView showRegisterPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/Register")
    public ModelAndView handleRegister(
            @Valid @ModelAttribute("user") User user,
            BindingResult result) {

        ModelAndView mv = new ModelAndView("register");

        if (result.hasErrors()) {
            return mv;
        }

        // Default student role
        user.setUserType("STUDENT");

        if (!userService.registerUser(user)) {
            mv.addObject("error", "An account with this username or email already exists.");
            return mv;
        }

        return new ModelAndView("redirect:/MainGallery/Login");
    }


    /* ============================================================
       DASHBOARDS
       ============================================================ */

    @GetMapping("/studentDashboard")
    public ModelAndView studentDashboard() {
        return new ModelAndView("studentDashboard");
    }

    @GetMapping("/adminDashboard")
    public ModelAndView adminDashboard() {
        ModelAndView mav = new ModelAndView("adminDashboard");
        mav.addObject("totalShowcases", showcaseService.totalShowcases());
        mav.addObject("liveShowcases", showcaseService.liveShowcases());
        mav.addObject("draftShowcases", showcaseService.draftShowcases());
        mav.addObject("totalProjects", projectService.totalProjects());
        mav.addObject("recentShowcases", showcaseService.recentShowcases());
        mav.addObject("recentProjects", projectService.recentProjects());
        return mav;
    }

    @GetMapping("/seniorAdminDashboard")
    public ModelAndView seniorAdminDashboard() {
        return new ModelAndView("seniorAdminDashboard");
    }


    /* ============================================================
       HOMEPAGE (SINGLE MAPPING WITH FILTERS)
       ============================================================ */

    @GetMapping({"/HomePage", ""})
    public ModelAndView home(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy) {

        ModelAndView mav = new ModelAndView("homepage");

        // ✅ Let ProjectService handle pagination + keyword + category + sorting
        Page<Project> projectPage =
                projectService.getPaginatedProjects(page, keyword, category, sortBy);

        // Generate thumbnails for project cards
        generateProjectThumbnails(projectPage.getContent());

        // Generate thumbnails for hero showcase carousel
        List<Showcase> showcases = generateShowcaseThumbnails();

        mav.addObject("projectPage", projectPage);
        mav.addObject("AllLiveShowcases", showcases);

        // Pass current filters to the view
        mav.addObject("keyword", keyword);
        mav.addObject("category", category);
        mav.addObject("sortBy", sortBy);

        // Saved project IDs for employer bookmark icons
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && user.getUserType() != null
                && user.getUserType().equalsIgnoreCase("employer")) {
            mav.addObject("savedIds", savedProjectService.getSavedProjectIds(user));
        }

        return mav;
    }


    /* ============================================================
       SEARCH (SEPARATE PAGE)
       ============================================================ */

    @GetMapping("/searchProjects")
    public ModelAndView searchProjects(@RequestParam(required = false) String keyword) {
        ModelAndView mav = new ModelAndView("searchResults");
        mav.addObject("projects", projectService.searchProjects(keyword));
        mav.addObject("keyword", keyword);
        return mav;
    }


    /* ============================================================
       SHOWCASE VIEW + REMOVE PROJECT
       ============================================================ */

    @GetMapping("/browseShowcase/{showcaseId}")
    public ModelAndView viewShowcaseById(@PathVariable Long showcaseId) {
        ModelAndView mav = new ModelAndView("browseShowcase");
        Showcase showcase = showcaseService.findById(showcaseId);

        if (showcase != null) {
            mav.addObject("showcase", showcase);
            mav.addObject("projects", showcase.getProjects());
        } else {
            mav.addObject("error", "Showcase not found.");
            mav.addObject("projects", List.of());
        }

        return mav;
    }

    @PostMapping("/browseShowcase/{showcaseId}/removeProject/{projectId}")
    public String removeProjectFromShowcase(
            @PathVariable Long showcaseId,
            @PathVariable Long projectId) {

        showcaseService.removeProjectFromShowcase(showcaseId, projectId);
        return "redirect:/MainGallery/browseShowcase/" + showcaseId;
    }


    /* ============================================================
       THUMBNAIL HELPERS
       ============================================================ */

    private void generateProjectThumbnails(List<Project> projects) {
        try {
            String base = "src/main/resources/static/assets/images/projects/";
            for (Project project : projects) {
                File img = new File(base + project.getProjectHeroImage());
                File thumb = new File(base + "thumbnail/thumb_" + img.getName());
                thumbnailService.generateThumbnail(img, thumb);
            }
        } catch (IOException ignored) {}
    }

    private List<Showcase> generateShowcaseThumbnails() {
        List<Showcase> showcases = showcaseService.findAllLive();
        try {
            String base = "src/main/resources/static/assets/images/showcases/";
            for (Showcase sc : showcases) {
                File img = new File(base + sc.getImage());
                File thumb = new File(base + "thumbnail/thumb_" + img.getName());
                thumbnailService.generateThumbnailShowcase(img, thumb);
            }
        } catch (IOException ignored) {}
        return showcases;
    }

}
