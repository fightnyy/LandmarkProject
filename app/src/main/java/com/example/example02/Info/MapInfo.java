package com.example.example02.Info;


import androidx.fragment.app.Fragment;

public class MapInfo {
    private String location1;
    private String location2;
    private String name;
    private String explain;

    public MapInfo(){ }

    public MapInfo(String location1, String location2, String name, String explain) {
        this.location1 = location1;
        this.location2 = location2;
        this.name = name;
        this.explain = explain;
    }

    public String getExplain(){ return this.explain; }
    public void setExplain(String explain){ this.explain = explain; }

    public String getLocation1() {
        return this.location1;
    }
    public void setLocation1(String location1) { this.location1 = location1; }

    public String getLocation2(){ return this.location2; }
    public void setLocation2(String location2){ this.location2 = location2; }

    public String getName(){ return this.name; }
    public void setName(String name){ this.name = name; }

}
