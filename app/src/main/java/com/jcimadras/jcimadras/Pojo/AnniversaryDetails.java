package com.jcimadras.jcimadras.Pojo;

public class AnniversaryDetails {
    private String JCName, JcrtName, Date;

    public AnniversaryDetails() {
    }

    public AnniversaryDetails(String jcName, String jcrtName, String date) {
        this.JCName = jcName;
        this.JcrtName = jcrtName;
        this.Date = date;
    }

    public String getJCName() {
        return JCName;
    }

    public void setJCName(String JCName) {
        this.JCName = JCName;
    }

    public String getJcrtName() {
        return JcrtName;
    }

    public void setJcrtName(String jcrtName) {
        JcrtName = jcrtName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
