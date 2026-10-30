package com.poulogy.site.jpa;

import com.poulogy.site.modules.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileJPA extends JpaRepository<Profile, Long> {
}
