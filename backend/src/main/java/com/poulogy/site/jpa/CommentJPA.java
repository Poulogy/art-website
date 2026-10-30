package com.poulogy.site.jpa;

import com.poulogy.site.modules.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentJPA extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByDate(Long postId);
}
