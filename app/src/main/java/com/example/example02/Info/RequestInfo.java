package com.example.example02.Info;

public class RequestInfo {

    public String location;
    public String Reason;
    public String detailLocation;
    public String publisher;

    public RequestInfo(String location,String detailLocation, String Reason,String publisher)
    {
        this.detailLocation=detailLocation;
        this.publisher=publisher;
        this.Reason=Reason;
        this.location=location;
    }



//    public String getPublisher() {
//        return publisher;
//    }
//
//    public void setPublisher(String publisher) {
//        this.publisher = publisher;
//    }
}

