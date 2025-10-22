package com.example.cp470project;

import java.io.Serializable;
public class Resource implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String description;
    private String url;

    public Resource(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
}
