package io.grindallday.endrone_mobile_app.model;

import androidx.annotation.NonNull;

public class Pump {
    String id;
    String name;
    String stationId;
    String type;

    public Pump(String id, String name, String stationId, String type) {
        this.id = id;
        this.name = name;
        this.stationId = stationId;
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStationId() {
        return stationId;
    }

    public String getType() {
        return type;
    }
}
