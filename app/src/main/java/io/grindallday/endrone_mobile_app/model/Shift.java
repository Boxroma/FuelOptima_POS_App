package io.grindallday.endrone_mobile_app.model;


import com.google.firebase.Timestamp;

public class Shift {
    String id;
    String station_id;
    String user_id;
    String user_name;
    Timestamp start_time;
    Timestamp end_time;
    boolean active;

    public Shift(String id, String station_id, String user_id, String user_name, Timestamp start_time, Timestamp end_time, boolean active) {
        this.id = id;
        this.station_id = station_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.active = active;
    }

    public String getStation_id() {
        return station_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public boolean isActive() {
        return active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }



}
