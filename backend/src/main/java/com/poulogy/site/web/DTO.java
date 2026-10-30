package com.poulogy.site.web;

import com.poulogy.site.modules.Comment;
import com.poulogy.site.modules.Post;
import com.poulogy.site.modules.PostType;
import com.poulogy.site.modules.Profile;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;


public final class DTO {

    private DTO() {
    }
    public record Login(@NotBlank String password) {
    }
    public record PostDTO(Long id, String title, String text, PostType postType,
                          String postPath, Instant date) {
        public static PostDTO of(Post p) {
            return new PostDTO(p.getId(), p.getTitle(), p.getContent(), p.getType(),
                    "/media/" + p.getPostPath(), p.getDataCreation());
        }
    }
    public record PostPage(List<PostDTO> items, int page, int totalPages) {}
    public record PostUpdate(@NotBlank @Size(max = 200) String title, String description) {}


    public record CommentRequest(@NotBlank @Size(max = 50) String name,
                                 @NotBlank @Size(max = 1000) String text,
                                 @Size(max = 200) String website) {}

    public record CommentDTO(Long id, String aName, String text, Instant date) {
        public static CommentDTO of(Comment c) {
            return new CommentDTO(c.getId(), c.getaName(), c.getText(), c.getDate());
        }
    }
    public record ProfileDTO(Long id, @NotBlank @Size(max = 100) String name, String bio,
                             @Pattern(regexp = "^$|^https://.{1,480}$") String iconPath,
                             @Pattern(regexp = "^$|^https://.{1,480}$") String donatePath) {
        public static ProfileDTO of(Profile f) {
            return new ProfileDTO(f.getId(), f.getName(), f.getBio(),
                    f.getIconPath(), f.getDonate());
        }
    }
}
