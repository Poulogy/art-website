package com.poulogy.site.web;

import com.poulogy.site.configuration.PassLimiter;
import com.poulogy.site.jpa.CommentJPA;
import com.poulogy.site.jpa.PostJPA;
import com.poulogy.site.jpa.ProfileJPA;
import com.poulogy.site.modules.Comment;
import com.poulogy.site.modules.Profile;
import com.poulogy.site.web.DTO.*;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentJPA comments;
    private final PostJPA posts;
    private final ProfileJPA profiles;
    private final PassLimiter limiter;

    public CommentController(CommentJPA comments, PostJPA posts,
                             ProfileJPA profiles, PassLimiter limiter) {
        this.comments = comments; this.posts = posts; this.profiles = profiles; this.limiter = limiter;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDTO> list(@PathVariable Long postId) {
        return comments.findByPostIdOrderByDate(postId).stream().map(CommentDTO::of).toList();
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO add(@PathVariable Long postId, @Valid @RequestBody CommentRequest req,
                              HttpServletRequest request, Authentication auth) {
        var post = posts.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (req.website() != null && !req.website().isBlank())
            return new CommentDTO(0L, req.name(), req.text(), Instant.now());

        if (!AuthController.isAdmin(auth)) {
            String authorName = profiles.findById(1L).map(Profile::getName).orElse("");
            if (req.name().trim().equalsIgnoreCase(authorName.trim()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Это имя занято автором сайта. Выберите другое.");
            if (!limiter.allow("comment:" + request.getRemoteAddr(), 5, 60_000))
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Слишком часто. Подождите минуту.");
        }

        Comment c = new Comment();
        c.setPost(post);
        c.setaName(req.name().trim());
        c.setText(req.text().trim());
        return CommentDTO.of(comments.save(c));
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        comments.deleteById(id);
    }
}
