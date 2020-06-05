package com.example.example02.Info;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo {
    private String postText;
    private String photoUrl;
    private String publisher;
    private String location;
    private String area;
    @ServerTimestamp
    private Date createdAt;

    public PostInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
    public PostInfo(String postText, String photoUrl, String publisher, String location, String area) {
        this.postText = postText;
        this.photoUrl = photoUrl;
        this.publisher = publisher;
        this.location = location;
        this.area = area;
    }

    public PostInfo(String postText, String photoUrl, String publisher, String location, String area, Date createdAt) {
        this.postText = postText;
        this.photoUrl = photoUrl;
        this.publisher = publisher;
        this.location = location;
        this.area = area;
        this.createdAt = createdAt;
    }

    public String getPhotoUrl(){
        return this.photoUrl;
    }
    public String getPostText() { return this.postText; }
    public String getArea() { return this.area; }
    public String getPublisher() { return this.publisher; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postText", postText);
        result.put("photoUrl", photoUrl);
        result.put("publisher", publisher);
        result.put("location", location);
        result.put("area", area);
        result.put("createdAt", createdAt);
        return result;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
