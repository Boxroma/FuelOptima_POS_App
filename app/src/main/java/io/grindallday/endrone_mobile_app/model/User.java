package io.grindallday.endrone_mobile_app.model;

import java.util.Date;

public class User {
    private String uid;
    private String firstName;
    private String secondName;
    private String email;
    private String date;
    private String nrc;
    private String stationId;
    private String stationName;
    private String stationAddress;
    private String role;

    public User(String uid,
                String firstName,
                String secondName,
                String email,
                String date,
                String nrc,
                String stationId,
                String stationName,
                String stationAddress,
                String role) {
        this.uid = uid;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.date = date;
        this.nrc = nrc;
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationAddress = stationAddress;
        this.role = role;
    }

    public User() {
        // Empty constructor
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String  getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(String stationAddress) {
        this.stationAddress = stationAddress;
    }


}
