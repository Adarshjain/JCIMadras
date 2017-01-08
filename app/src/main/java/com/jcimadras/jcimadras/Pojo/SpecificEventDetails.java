package com.jcimadras.jcimadras.Pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SpecificEventDetails {
    private String EventName, EventDesc;
    private int limit = -1,total = 0;
    private String Coordinates;
    private String Date;
    private Boolean photoAdded;
    private String imgPath,imgPathName;

    public SpecificEventDetails() {
    }//Empty constructor for Firebase

    public SpecificEventDetails(String eventName, String eventDesc, String Date,Boolean photoAdded) {
        this.EventName = eventName;
        this.EventDesc = eventDesc;
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
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getImgPathName() {
        return imgPathName;
    }

    public void setImgPathName(String imgPathName) {
        this.imgPathName = imgPathName;
    }
}
