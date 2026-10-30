package com.poulogy.site.jpa;

import com.poulogy.site.modules.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJPA extends JpaRepository<Post, Long> {
}
