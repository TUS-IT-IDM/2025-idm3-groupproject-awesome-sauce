package idm3.project.gallery.controllers;

import idm3.project.gallery.model.Project;
import idm3.project.gallery.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping(value = {"/project", "/project/"})
public class ProjectController {

    private static final String UPLOAD_DIR = "src/main/resources/static/assets/images/projects";

    @Autowired
    private ProjectService projectService;

    @GetMapping("/add")
    public ModelAndView showAddProjectForm() {
        return new ModelAndView("/addProject", "newProject", new Project());
    }

//    //NEW CODE
//    @GetMapping("/details/{id}")
//    public ModelAndView showProjectDetails(@PathVariable("id") long id) {
//        return projectService.findOne(id)
//                .map(project -> new ModelAndView("/projectDetails", "project", project))
//                .orElseGet(() -> new ModelAndView("/error", "error", "Project not found"));
//    }
//    //NEW CODE



    @PostMapping("/addProject")
    public ModelAndView addAProject(@ModelAttribute Project project,
//                                    @Valid Project product,
                                    @RequestParam("imageFile") MultipartFile imageFile,
                                    BindingResult result) throws IOException {
        if (result.hasErrors()) {
            return new ModelAndView("/addProject", "newProject", project)
                    .addObject("errors", result.getAllErrors());
        }
//        else {
//
//            try {
//                projectService.saveProject(project, file);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return new ModelAndView("/error", "error", "Image upload failed");
//            }
//            return new ModelAndView("redirect:/product/allProducts");
//        }


        if (!imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            System.out.println("**********" + uploadPath.toAbsolutePath().toString());
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            // Save the uploaded file
            imageFile.transferTo(uploadPath.resolve(fileName));

            // Store only filename in DB
            project.setProjectHeroImage(fileName);

            projectService.uploadProjectHeroPicture(project,imageFile);
        }

        projectService.saveProject(project);
        return new ModelAndView("redirect:/project/allProjects");
    }

    @RequestMapping(value = {"/allProjects", ""})
    public ModelAndView displayAllProjects() {
        return new ModelAndView("/studentDashboard", "projects", projectService.findAllProjects());
    }

    @PostMapping("/delete")
    public ModelAndView deleteProject(@RequestParam("ProjectID") long id) {
        if (projectService.findOne(id).isEmpty()) {
            return new ModelAndView("/error", "error", "Project not found");
        } else {
            projectService.deleteById(id);
            return new ModelAndView("redirect:/project/allProjects");
        }
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditProjectForm(@PathVariable("id") long id) {
        if (projectService.findOne(id).isEmpty()) {
            return new ModelAndView("/error", "error", "Project not found");
        } else {
            return new ModelAndView("/editProject", "aProject", projectService.findOne(id).get());
        }
//        if (newImage != null && !newImage.isEmpty()) {
//            project.setProjectHeroImage(storeImage(newImage));
//        } else {
//            project.setProjectHeroImage(existingProject.getProjectHeroImage());
//        }
   }

    @PostMapping("/saveProject")
    public ModelAndView saveOrUpdateProject(@ModelAttribute("aProject") Project project,
                                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                            BindingResult result) throws IOException {

        if (result.hasErrors()) {
            String viewName = (project.getProjectId() == null) ? "/addProject" : "/editProject";
            return new ModelAndView(viewName);
        }


        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            imageFile.transferTo(new File(UPLOAD_DIR + fileName));
            project.setProjectHeroImage(fileName);
        }

        projectService.saveProject(project);
        return new ModelAndView("redirect:/project/allProjects");
    }
    @GetMapping("/search")
    public ModelAndView searchProjects(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Project> searchResults = projectService.searchProjects(keyword);
        return new ModelAndView("/viewAll", "projects", searchResults);
    }
}

