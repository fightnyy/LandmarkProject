package com.example.example02;

public class PostInfo {
    private String writingText;
    private String photoUrl;

    public PostInfo(String writingText, String photoUrl) {
        this.writingText = writingText;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return this.writingText;
    }

    public void setName(String name) { this.writingText = name; }

    public String getPhotoUrl(){ return this.photoUrl; }

    public void setPhotoUrl(String photoUrl){ this.photoUrl = photoUrl; }
}
