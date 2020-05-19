package com.example.example02;

import java.util.Date;

public class PostInfo {
    private String writingText;
    private String photoUrl;
    private String publisher;
    private Date createdAt;

    public PostInfo(String writingText, String photoUrl, String publisher, Date createdAt) {
        this.writingText = writingText;
        this.photoUrl = photoUrl;
        this.publisher = publisher;
        this.createdAt = createdAt;
    }

    public String getName() {
        return this.writingText;
    }
    public void setName(String name) { this.writingText = name; }

    public String getPhotoUrl(){ return this.photoUrl; }
    public void setPhotoUrl(String photoUrl){ this.photoUrl = photoUrl; }

    public String getpublisher(){ return this.publisher; }
    public void setpublisher(String publisher){ this.publisher = publisher; }

    public Date getcreatedAt(){ return this.createdAt; }
    public void setcreatedAtr(Date createdAt){ this.createdAt = createdAt; }
}
