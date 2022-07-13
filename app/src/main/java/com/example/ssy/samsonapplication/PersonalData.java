package com.example.ssy.samsonapplication;


public class PersonalData {
    private String member_id;
    private String member_pwd;
    private String member_date;
    private String member_name;
    private String  option;
    private Boolean  redDot;
    private Boolean yellowDot;
    private int density;
    private int thickness;

    public String getMember_id() {
        return member_id;
    }

    public String getMember_pwd() {
        return member_pwd;
    }
    public String getMember_date() {
        return member_date;
    }
    public String getMember_name() {
        return member_name;
    }

    public String getMember_option() {
        return option;
    }

    public Boolean getMember_redDot() {
        return redDot;
    }

    public Boolean getMember_yellowDot() {
        return yellowDot;
    }

    public int getMember_density() {
        return density;
    }

    public int getMember_thickness() {
        return thickness;
    }


    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public void setMember_pwd(String member_pwd) {
        this.member_pwd = member_pwd;
    }
    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String setMember_date() {
        return member_date;
    }

    public String setMember_option() {
        return option;
    }


    public Boolean setMember_redDot() {
        return redDot;
    }

    public Boolean setMember_yellowDot() {
        return yellowDot;
    }

    public int setMember_density() {
        return density;
    }

    public int setMember_thickness() {
        return thickness;
    }


}