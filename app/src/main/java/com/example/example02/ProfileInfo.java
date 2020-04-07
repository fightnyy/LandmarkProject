package com.example.example02;

public class ProfileInfo {
    private String name;
    private String address;
    private String photoUrl;

    public ProfileInfo(String name, String address, String photoUrl) {
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) { this.name = name; }

    public String getAddress(){ return this.address; }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl(){ return this.photoUrl; }

    public void setPhotoUrl(String photoUrl){ this.photoUrl = photoUrl; }
}
