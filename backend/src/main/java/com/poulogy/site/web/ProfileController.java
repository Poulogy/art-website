package com.poulogy.site.web;

import com.poulogy.site.web.DTO.*;
import com.poulogy.site.jpa.ProfileJPA;
import com.poulogy.site.modules.Profile;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileJPA repo;
    public ProfileController(ProfileJPA repo) { this.repo = repo; }

    @GetMapping
    public ProfileDTO get() { return ProfileDTO.of(repo.findById(1L).orElseThrow()); }

    @PutMapping
    public ProfileDTO update(@Valid @RequestBody ProfileDTO req) {
        Profile p = repo.findById(1L).orElseThrow();
        p.setName(req.name());
        p.setBio(req.bio());
        p.setIconPath(blankToNull(req.iconPath()));
        p.setDonate(blankToNull(req.donatePath()));
        return ProfileDTO.of(repo.save(p));
    }

    private static String blankToNull(String s) { return s == null || s.isBlank() ? null : s; }
}