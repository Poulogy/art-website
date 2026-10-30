package com.poulogy.site.modules;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "text")
    private String text;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private PostType type;
    @Column(name = "post_path", nullable = false)
    private String postPath;
    @Column(name = "data_creation", nullable = false)
    private Instant dataCreation = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return text;
    }

    public void setContent(String content) {
        this.text = content;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getPostPath() {
        return postPath;
    }

    public void setPostPath(String postPath) {
        this.postPath = postPath;
    }

    public Instant getDataCreation() {
        return dataCreation;
    }

    public void setDataCreation(Instant dataCreation) {
        this.dataCreation = dataCreation;
    }
}
