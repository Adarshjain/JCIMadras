package com.jcimadras.jcimadras.Pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ParticipantDetails {
    private String Name;
    private int senior,junior,guest,total;

    public ParticipantDetails(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getGuest() {
        return guest;
    }

    public void setGuest(int guest) {
        this.guest = guest;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSenior() {
        return senior;
    }

    public void setSenior(int senior) {
        this.senior = senior;
    }

    public int getJunior() {
        return junior;
    }

    public void setJunior(int junior) {
        this.junior = junior;
    }
}
