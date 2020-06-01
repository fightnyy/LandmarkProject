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
}
