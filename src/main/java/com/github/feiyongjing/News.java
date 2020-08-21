package com.github.feiyongjing;

import java.time.Instant;

public class News {
    private Integer id;
    private String url;
    private String content;
    private String title;
    private Instant createdAt;
    private Instant modifiedAt;

    public News() {
    }

    public News(String url, String content, String title) {
        this.url = url;
        this.content = content;
        this.title = title;
    }

    public News(News old) {
        this.id = old.id;
        this.url = old.url;
        this.content = old.content;
        this.title = old.title;
        this.createdAt = old.createdAt;
        this.modifiedAt = old.modifiedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAtd(Instant creadAt) {
        this.createdAt = creadAt;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }
}
