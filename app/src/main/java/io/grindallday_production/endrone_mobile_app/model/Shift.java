package io.grindallday_production.endrone_mobile_app.model;

import com.google.firebase.Timestamp;

public class Shift {
    String id;
    String name;
    Timestamp start;
    Timestamp stop;
    String stationId;
    String status;

    public Shift(String id, String name, Timestamp start, Timestamp stop, String stationId, String status) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.stationId = stationId;
        this.status = status;
    }

    public Shift() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getStop() {
        return stop;
    }

    public void setStop(Timestamp stop) {
        this.stop = stop;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
