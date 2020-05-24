package com.example.example02.Info;

import java.util.Date;

public class PostInfo {
    private String postText;
    private String photoUrl;
    private String publisher;
    private Date createdAt;

    public PostInfo(String postText, String photoUrl, String publisher, Date createdAt) {
        this.postText = postText;
        this.photoUrl = photoUrl;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }

    public String getpostText() {
        return this.postText;
    }
    public void setpostText(String postText) { this.postText = postText; }

    public String getPhotoUrl(){ return this.photoUrl; }
    public void setPhotoUrl(String photoUrl){ this.photoUrl = photoUrl; }

    public String getpublisher(){ return this.publisher; }
    public void setpublisher(String publisher){ this.publisher = publisher; }

    public Date getcreatedAt(){ return this.createdAt; }
    public void setcreatedAt(Date createdAt){ this.createdAt = createdAt; }
}
