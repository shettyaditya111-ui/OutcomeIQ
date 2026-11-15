package com.outcomeiq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.outcomeiq.entity.ProgramOutcome;
import com.outcomeiq.repository.ProgramOutcomeRepository;

@Controller
public class ProgramOutcomeController {

private final ProgramOutcomeRepository repo;
public ProgramOutcomeController(ProgramOutcomeRepository repo) {
    this.repo = repo;
}
@GetMapping("/pos")
public String pos(Model model) {
    model.addAttribute("pos", repo.findAll());
    return "pos";
}
@PostMapping("/addPO")
public String addPO(@ModelAttribute ProgramOutcome po) {
    repo.save(po);
    return "redirect:/pos";
}
@GetMapping("/deletePO/{id}")
public String deletePO(@PathVariable Long id) {
    repo.deleteById(id);
    return "redirect:/pos";
}

}