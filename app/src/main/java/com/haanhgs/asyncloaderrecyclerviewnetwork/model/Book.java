package com.haanhgs.asyncloaderrecyclerviewnetwork.model;

import java.util.List;

public class Book {

    private String title;
    private List<String> authors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
