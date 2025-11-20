package idm3.project.gallery.controllers;

import idm3.project.gallery.service.ShowcaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



    @Controller
    public class ShowcaseController {

        @Autowired
        private ShowcaseService showcaseService;  // <-- not static

        @PostMapping("/showcase/add")
        public String addToShowcase(@RequestParam("projectId") Long projectId) {

            Long defaultShowcaseId = 1L; // example
            showcaseService.addProjectToShowcase(projectId, defaultShowcaseId);

            return "redirect:/project/allProjects";
        }
    }

