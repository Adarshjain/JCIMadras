package com.jcimadras.jcimadras.Pojo;

import android.net.Uri;

public class Images {
    private String Desc, ImageUrl, thumb, ImagePathName;
    private Uri Imageuri;

    public Images() {
    } //Empty Constructor for Firebase

    public Images(Uri imageUri) {
        this.Imageuri = imageUri;
    }

    public Images(String desc, String imageUrl, String imagePathName, String thumb) {
        this.Desc = desc;
        this.ImageUrl = imageUrl;
        this.thumb = thumb;
        this.ImagePathName = imagePathName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public Uri getImageuri() {
        return Imageuri;
    }

    public void setImageuri(Uri imageuri) {
        Imageuri = imageuri;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getImagePathName() {
        return ImagePathName;
    }

    public void setImagePathName(String imagePathName) {
        ImagePathName = imagePathName;
    }
}
