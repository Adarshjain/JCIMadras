package com.jcimadras.jcimadras.Pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class HeadDetails {
    private String PresidentName, LadiesName, PrePath, LadPath, PreUrl, LadUrl, Year;

    public HeadDetails() {
    }//For Firebase

    public String getPresidentName() {
        return PresidentName;
    }

    public void setPresidentName(String presidentName) {
        PresidentName = presidentName;
    }

    public String getLadiesName() {
        return LadiesName;
    }

    public void setLadiesName(String ladiesName) {
        LadiesName = ladiesName;
    }

    public String getPrePath() {
        return PrePath;
    }

    public void setPrePath(String prePath) {
        PrePath = prePath;
    }

    public String getLadPath() {
        return LadPath;
    }

    public void setLadPath(String ladPath) {
        LadPath = ladPath;
    }

    public String getPreUrl() {
        return PreUrl;
    }

    public void setPreUrl(String preUrl) {
        PreUrl = preUrl;
    }

    public String getLadUrl() {
        return LadUrl;
    }

    public void setLadUrl(String ladUrl) {
        LadUrl = ladUrl;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }
}
