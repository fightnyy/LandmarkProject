package com.example.example02.Info;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class CommentInfo {
    private String comment;
    private String publisher;
    private String key;

    public CommentInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public CommentInfo(String comment, String publisher, String key){
        this.comment = comment;
        this.publisher = publisher;
        this.key = key;
    }

    public String getComment(){ return this.comment; }
    public String getPublisher() { return this.publisher; }
    public String getKey() { return this.key; }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("comment", comment);
        result.put("publisher", publisher);
        result.put("key", key);
        return result;
    }
}
