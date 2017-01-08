package com.jcimadras.jcimadras.Pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class EventDetails {
    private String EventName, EventDesc;
    private int limit = -1;
    private String Coordinates;
    private String Date;
    private Boolean photoAdded;
    private String imgPath;
    private int Total = 0;

    public EventDetails() {}//Empty constructor for Firebase

    public EventDetails(String eventName, String eventDesc, String Date,Boolean photoAdded) {
        this.EventName = eventName;
        this.EventDesc = eventDesc;
        this.Date = Date;
        this.photoAdded = photoAdded;
    }

    public EventDetails(String EventName,String EventDesc,int limit,String Coordinates,String Date,Boolean photoAdded){
        this.EventName = EventName;
        this.EventDesc = EventDesc;
        this.limit = limit;
        this.Coordinates = Coordinates;
        this.Date = Date;
        this.photoAdded = photoAdded;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(String coordinates) {
        Coordinates = coordinates;
    }


    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getEventDesc() {
        return EventDesc;
    }

    public void setEventDesc(String eventDesc) {
        EventDesc = eventDesc;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Boolean getPhotoAdded() {
        return photoAdded;
    }

    public void setPhotoAdded(Boolean photoAdded) {
        this.photoAdded = photoAdded;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }
}
