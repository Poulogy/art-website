package com.poulogy.site.web;

import com.poulogy.site.configuration.FileStorageService;
import com.poulogy.site.jpa.PostJPA;
import com.poulogy.site.modules.Post;
import com.poulogy.site.web.DTO.*;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostJPA posts;
    private final FileStorageService storage;

    public PostController(PostJPA posts, FileStorageService storage) {
        this.posts = posts; this.storage = storage;
    }

    @GetMapping
    public PostPage list(@RequestParam(defaultValue = "0") int page) {
        var p = posts.findAll(PageRequest.of(Math.max(page, 0), 12, Sort.by(Sort.Direction.DESC, "dataCreation")));
        return new PostPage(p.map(PostDTO::of).getContent(), p.getNumber(), p.getTotalPages());
    }

    @GetMapping("/{id}")
    public PostDTO get(@PathVariable Long id) {
        return PostDTO.of(find(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PostDTO create(@RequestParam String title,
                          @RequestParam(required = false) String description,
                          @RequestParam MultipartFile file) throws IOException {
        if (title.isBlank() || title.length() > 200)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название от 1 до 200 символов");
        var stored = storage.store(file);
        Post p = new Post();
        p.setTitle(title.trim());
        p.setContent(description);
        p.setType(stored.kind());
        p.setPostPath(stored.fileName());
        return PostDTO.of(posts.save(p));
    }

    @PutMapping("/{id}")
    public PostDTO update(@PathVariable Long id, @Valid @RequestBody PostUpdate req) {
        Post p = find(id);
        p.setTitle(req.title().trim());
        p.setContent(req.description());
        return PostDTO.of(posts.save(p));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Post p = find(id);
        posts.delete(p);
        storage.delete(p.getPostPath());
    }

    private Post find(Long id) {
        return posts.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

