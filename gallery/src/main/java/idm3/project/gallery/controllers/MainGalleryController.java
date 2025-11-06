// src/main/java/idm3/project/gallery/controllers/MainGalleryController.java
package idm3.project.gallery.controllers;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.model.User;
import idm3.project.gallery.service.ProjectService;
import idm3.project.gallery.service.SavedProjectService;
import idm3.project.gallery.service.ShowcaseService;
import idm3.project.gallery.service.ThumbnailService;
import idm3.project.gallery.service.UserService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    @Autowired private ServletContext servletContext;

    // Needed to populate "savedIds" on the homepage for employers (button state)
    @Autowired private SavedProjectService savedProjectService;

    // Login page
    @GetMapping("/Login")
    public ModelAndView showLoginPage() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("user", new User());
        return mav;
    }

    // Handle login
    @PostMapping("/Login")
    public ModelAndView handleLogin(@ModelAttribute("user") User user, HttpSession session) {
        ModelAndView mv = new ModelAndView("login"); // default back to login on error
        String email = user.getEmailAddress();
        String rawPassword = user.getPassword();

        User byEmail = userService.findByEmail(email);
        if (byEmail == null) {
            mv.addObject("emailError", "No account found for that email.");
            return mv;
        }

        if (!userService.passwordMatches(byEmail, rawPassword)) {
            mv.addObject("passwordError", "Incorrect password.");
            User prefill = new User();
            prefill.setEmailAddress(email);
            mv.addObject("user", prefill);
            return mv;
        }

        // success
        session.setAttribute("loggedInUser", byEmail);
        String type = byEmail.getUserType();
        if ("admin".equalsIgnoreCase(type)) {
            mv.setViewName("redirect:/MainGallery/adminDashboard");
        } else if ("student".equalsIgnoreCase(type)) {
            mv.setViewName("redirect:/MainGallery/studentDashboard");
        } else if ("senioradmin".equalsIgnoreCase(type)) {
            mv.setViewName("redirect:/MainGallery/seniorAdminDashboard");
        } else if ("employer".equalsIgnoreCase(type)) {
            // IMPORTANT: keep this URL; EmployerController renders the view "employerDashboard"
            mv.setViewName("redirect:/MainGallery/employerDashboard");
        } else {
            mv.setViewName("redirect:/MainGallery/dashboard");
        }
        mv.addObject("loggedInUser", byEmail.getEmailAddress());
        return mv;
    }

    // Logout
    @GetMapping("/Logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/MainGallery/Login";
    }

    @GetMapping("/profile")
    public ModelAndView viewProfile(HttpSession session) {
        ModelAndView mav = new ModelAndView("profile");
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            mav.addObject("user", loggedInUser);
        } else {
            mav.setViewName("redirect:/MainGallery/Login");
        }
        return mav;
    }

    // Registration
    @GetMapping("/Register")
    public ModelAndView showRegisterPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/Register")
    public ModelAndView handleRegister(@ModelAttribute("user") User user) {
        ModelAndView mav = new ModelAndView();
        user.setUserType("STUDENT");
        if (userService.registerUser(user)) {
            mav.setViewName("redirect:/MainGallery/Login");
            mav.addObject("message", "Registration successful! Please log in.");
        } else {
            mav.setViewName("register");
            mav.addObject("error", "Registration failed. Username or email might already exist.");
        }
        return mav;
    }

    @GetMapping("/studentDashboard")
    public ModelAndView studentDashboard() {
        return new ModelAndView("studentDashboard");
    }

    @GetMapping("/adminDashboard")
    public ModelAndView adminDashboard() {
        ModelAndView mav = new ModelAndView("adminDashboard");
        mav.addObject("totalShowcases", showcaseService.totalShowcases());
        mav.addObject("liveShowcases",  showcaseService.liveShowcases());
        mav.addObject("draftShowcases", showcaseService.draftShowcases());
        mav.addObject("totalProjects",  projectService.totalProjects());
        mav.addObject("recentShowcases", showcaseService.recentShowcases());
        mav.addObject("recentProjects",  projectService.recentProjects());
        return mav;
    }

    @GetMapping("/seniorAdminDashboard")
    public ModelAndView seniorAdminDashboard() {
        return new ModelAndView("seniorAdminDashboard");
    }

    /**
     * Home page (also handles root /MainGallery).
     * Adds "savedIds" when an employer is logged in – used to disable/label the "Save to list" buttons.
     */
    @RequestMapping(value = {"/HomePage", ""})
    public ModelAndView setUpIndexPageData(HttpSession session) {
        ModelAndView mav = new ModelAndView("homepage");

        List<Project> allProjects = projectService.findAllOrderedByCreationDate();
        generateThumbnailProject(allProjects);

        List<Showcase> allShowcases = generateThumbnailShowcases();

        mav.addObject("AllProjectsRecentFirst", allProjects);
        mav.addObject("AllLiveShowcases", allShowcases);

        // Populate savedIds for employers so UI can show "Saved" state
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null && "employer".equalsIgnoreCase(user.getUserType())) {
            mav.addObject("savedIds", savedProjectService.getSavedProjectIds(user));
        }

        return mav;
    }

    private List<Showcase> generateThumbnailShowcases() {
        List<Showcase> allShowcases = showcaseService.findAllLive();
        try {
            String imageDirPathShowcase = "src/main/resources/static/assets/images/showcases/";
            String thumbnailDirPathShowcase = "src/main/resources/static/assets/images/showcases/thumbnail/";
            for (Showcase showcase : allShowcases) {
                File image = new File(imageDirPathShowcase + "/" + showcase.getImage());
                File thumbnailFile = new File(thumbnailDirPathShowcase + "/" + "thumb_" + image.getName());
                thumbnailService.generateThumbnailShowcase(image, thumbnailFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allShowcases;
    }

    private void generateThumbnailProject(List<Project> allProjects) {
        try {
            String imageDirPathProject = "src/main/resources/static/assets/images/projects/";
            String thumbnailDirPathProject = "src/main/resources/static/assets/images/projects/thumbnail/";
            for (Project project : allProjects) {
                File image = new File(imageDirPathProject + "/" + project.getProjectHeroImage());
                File thumbnailFile = new File(thumbnailDirPathProject + "/" + "thumb_" + image.getName());
                thumbnailService.generateThumbnail(image, thumbnailFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/searchProjects")
    public ModelAndView searchProjects(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Project> searchResults = projectService.searchProjects(keyword);
        ModelAndView mav = new ModelAndView("searchResults");
        mav.addObject("projects", searchResults);
        mav.addObject("keyword", keyword);
        return mav;
    }

    @GetMapping("/browseShowcase/{showcaseId}")
    public ModelAndView viewShowcaseById(@PathVariable("showcaseId") Long showcaseId) {
        ModelAndView mav = new ModelAndView("browseShowcase");

        // 🧭 Get the showcase from the ShowcaseService
        Showcase showcase = showcaseService.findById(showcaseId);

        if (showcase != null) {
            // ✅ This is where showcase.getProjects() is used
            mav.addObject("showcase", showcase);
            mav.addObject("projects", showcase.getProjects());
        } else {
            mav.addObject("error", "Showcase not found");
            mav.addObject("projects", List.of()); // Empty list fallback
        }

        return mav;
    }






}
