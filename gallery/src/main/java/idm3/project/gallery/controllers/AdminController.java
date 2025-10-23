package idm3.project.gallery.controllers;

import idm3.project.gallery.model.Showcase;
import idm3.project.gallery.service.ShowcaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;                   // ✅ correct import
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller                                     // ✅ add this
@RequestMapping("/MainGallery/admin/showcases")
public class AdminController {

    private final ShowcaseService showcaseService;

    @Autowired
    public AdminController(ShowcaseService showcaseService) {
        this.showcaseService = showcaseService;
    }

    // Show the create form
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("showcase", new Showcase());
        return "admin/showcaseForm";
    }


    @PostMapping("/create")
    public String createSubmit(@ModelAttribute("showcase") Showcase showcase,
                               @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (showcase.getStatus() != null) {
                showcase.setStatus(showcase.getStatus().trim().toUpperCase());
            }
            showcaseService.saveShowcaseWithImage(showcase, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/MainGallery/admin/showcases"; // <-- go to list
    }

    @GetMapping
    public String listShowcases(org.springframework.ui.Model model) {
        model.addAttribute("showcases", showcaseService.findAll());
        return "admin/showcaseList";
    }



    @GetMapping("/edit/{id}")
    public String editShowcaseForm(@PathVariable("id") Long id, org.springframework.ui.Model model) {
        Showcase showcase = showcaseService.findById(id);
        if (showcase == null) {
            return "redirect:/MainGallery/admin/showcases";
        }
        model.addAttribute("showcase", showcase);
        return "admin/showcaseEdit"; // New template
    }


    @PostMapping("/edit/{id}")
    public String updateShowcase(@PathVariable("id") Long id,
                                 @ModelAttribute("showcase") Showcase updatedShowcase,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Showcase existing = showcaseService.findById(id);
        if (existing == null) {
            return "redirect:/MainGallery/admin/showcases";
        }

        try {
            // Update basic fields
            existing.setName(updatedShowcase.getName());
            existing.setDescription(updatedShowcase.getDescription());
            existing.setStatus(updatedShowcase.getStatus().trim().toUpperCase());

            // Replace image only if a new one was uploaded
            if (imageFile != null && !imageFile.isEmpty()) {
                showcaseService.saveShowcaseWithImage(existing, imageFile);
            } else {
                showcaseService.save(existing);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/MainGallery/admin/showcases";
    }

    // DELETE — remove a showcase by id
    @PostMapping("/delete/{id}")
    public String deleteShowcase(@PathVariable("id") Long id) {
        showcaseService.deleteById(id);
        return "redirect:/MainGallery/admin/showcases"; // back to the list
    }





}
