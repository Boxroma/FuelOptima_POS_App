package io.grindallday.endrone_mobile_app.model;

import com.google.firebase.Timestamp;

public class Client {
    private String uid;
    private String name;
    private String email;
    private String number;
    private Timestamp dateCreated;
    private String stationId;
    private Double currentBalance;

    public Client(String uid, String name, String email, String number, Timestamp dateCreated, String stationId, Double currentBalance) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.number = number;
        this.dateCreated = dateCreated;
        this.stationId = stationId;
        this.currentBalance = currentBalance;
    }

    public Client() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }
}
