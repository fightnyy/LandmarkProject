package com.example.example02.Info;


public class MapInfo {
    private String location1;
    private String location2;
    private String name;

    public MapInfo(String location1, String location2, String name) {
        this.location1 = location1;
        this.location2 = location2;
        this.name = name;
    }

    public String getLocation1() {
        return this.location1;
    }
    public void setLocation1(String location1) { this.location1 = location1; }

    public String getLocation2(){ return this.location2; }
    public void setLocation2(String location2){ this.location2 = location2; }

    public String getName(){ return this.name; }
    public void setName(String name){ this.name = name; }

}
