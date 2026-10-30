package com.poulogy.site.modules;

import jakarta.persistence.*;

@Entity
public class Profile {
    @Id
    private Long id = 1L;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "text")
    private String bio;
    @Column(name = "icon_path")
    private String iconPath;
    @Column(name = "donate")
    private String donate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDonate() {
        return donate;
    }

    public void setDonate(String donate) {
        this.donate = donate;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
