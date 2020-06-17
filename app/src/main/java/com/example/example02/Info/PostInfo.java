package com.example.example02.Info;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostInfo{
    private String postText;
    private String photoUrl;
    private String publisher;
    private String location;
    private String area;
    private String key;
    @ServerTimestamp
    private Date createdAt;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public PostInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
    public PostInfo(String postText, String photoUrl, String publisher, String location, String area, String Key) {
        this.postText = postText;
        this.photoUrl = photoUrl;
        this.publisher = publisher;
        this.location = location;
        this.area = area;
        this.key = key;
    }

    public PostInfo(String postText, String photoUrl, String publisher, String location, String area, Date createdAt, String key) {
        this.postText = postText;
        this.photoUrl = photoUrl;
        this.publisher = publisher;
        this.location = location;
        this.area = area;
        this.createdAt = createdAt;
        this.key = key;
    }

    public String getPhotoUrl(){ return this.photoUrl; }
    public void setPostText(String postText) { this.postText = postText; }

    public String getPostText() { return this.postText; }

    public String getArea() { return this.area; }

    public String getPublisher() { return this.publisher; }

    public String getKey() { return this.key; }

    //public Date getCreatedAt(){ return getCreatedAt(); }

    public int getStarCount() { return this.starCount; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postText", postText);
        result.put("photoUrl", photoUrl);
        result.put("publisher", publisher);
        result.put("location", location);
        result.put("area", area);
        result.put("createdAt", createdAt);
        result.put("key", key);
        return result;
    }
}
