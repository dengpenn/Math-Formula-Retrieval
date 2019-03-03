package com.search.engine.mathsearch.model;

import javax.persistence.Entity;

@Entity
public class Article {
    private String Path;

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }
}
