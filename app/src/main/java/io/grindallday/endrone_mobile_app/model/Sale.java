package io.grindallday.endrone_mobile_app.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Sale {

    private String uid;
    private String attendant_id;
    private String attendantName;
    private String clientId;
    private String clientType;
    private String clientName;
    private String station_id;
    private String stationName;
    private Timestamp time;
    private Double Total;
    private List<Product> productList;

    public Sale(String uid,
                String attendantId,
                String attendantName,
                String clientId,
                String clientType,
                String clientName,
                String stationId,
                String stationName,
                Timestamp time,
                Double total,
                List<Product> productList) {
        this.uid = uid;
        this.attendant_id = attendantId;
        this.attendantName = attendantName;
        this.clientId = clientId;
        this.clientType = clientType;
        this.clientName = clientName;
        this.station_id = stationId;
        this.stationName = stationName;
        this.time = time;
        Total = total;
        this.productList = productList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAttendant_id() {
        return attendant_id;
    }

    public void setAttendant_id(String attendant_id) {
        this.attendant_id = attendant_id;
    }

    public String getAttendantName() {
        return attendantName;
    }

    public void setAttendantName(String attendantName) {
        this.attendantName = attendantName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Double getTotal() {
        return Total;
    }

    public void setTotal(Double total) {
        Total = total;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

}
