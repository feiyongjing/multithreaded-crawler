package com.github.feiyongjing;

public class News {
    private Integer id;
    private String url;
    private String content;
    private String title;

    public News(String url, String content, String title) {
        this.url = url;
        this.content = content;
        this.title = title;
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
