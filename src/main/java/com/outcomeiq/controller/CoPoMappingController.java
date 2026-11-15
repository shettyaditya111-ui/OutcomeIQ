package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.*;
import com.outcomeiq.repository.*;

@Controller
public class CoPoMappingController {

    private final CoPoMappingRepository repo;
    private final CourseOutcomeRepository coRepo;
    private final ProgramOutcomeRepository poRepo;

    public CoPoMappingController(CoPoMappingRepository repo,
            CourseOutcomeRepository coRepo,
            ProgramOutcomeRepository poRepo) {
        this.repo = repo;
        this.coRepo = coRepo;
        this.poRepo = poRepo;
    }

    @GetMapping("/mappings")
    public String mappings(Model model) {
        model.addAttribute("mappings", repo.findAll());
        model.addAttribute("cos", coRepo.findAll());
        model.addAttribute("pos", poRepo.findAll());
        return "mappings";
    }

    @PostMapping("/addMapping")
    public String addMapping(@RequestParam Long coId,
            @RequestParam Long poId,
            @RequestParam int mappingStrength) {

        if (mappingStrength < 1 || mappingStrength > 3) {
            return "redirect:/mappings";
        }

        boolean exists = repo.findAll().stream().anyMatch(m ->
            m.getCourseOutcome().getId().equals(coId) &&
            m.getProgramOutcome().getId().equals(poId)
        );

        if (exists) {
            return "redirect:/mappings";
        }

        CoPoMapping mapping = new CoPoMapping();

        mapping.setCourseOutcome(coRepo.findById(coId).orElseThrow());
        mapping.setProgramOutcome(poRepo.findById(poId).orElseThrow());
        mapping.setMappingStrength(mappingStrength);

        repo.save(mapping);

        return "redirect:/mappings";
    }

    @GetMapping("/deleteMapping/{id}")
    public String deleteMapping(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/mappings";
    }
}