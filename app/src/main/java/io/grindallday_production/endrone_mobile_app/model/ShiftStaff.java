package io.grindallday_production.endrone_mobile_app.model;


import com.google.firebase.Timestamp;

public class ShiftStaff {
    String id;
    String shiftId;
    String stationId;
    String userId;
    String userName;
    Timestamp startTime;
    Timestamp endTime;
    boolean active;
    Double totalSales;
    Double expectedCash;
    boolean reconciled;

    public ShiftStaff(String id, String shiftId, String stationId, String userId, String userName, Timestamp startTime, Timestamp endTime, boolean active, Double totalSales, Double expectedCash) {
        this.id = id;
        this.shiftId = shiftId;
        this.stationId = stationId;
        this.userId = userId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
        this.totalSales = totalSales;
        this.expectedCash = expectedCash;
        this.reconciled = false;
    }

    public ShiftStaff() {

    }

    public ShiftStaff(String id, String shiftId, String stationId, String userId, String userName, Timestamp startTime, Timestamp endTime, boolean active, Double totalSales, Double expectedCash, Boolean reconciled) {
        this.id = id;
        this.shiftId = shiftId;
        this.stationId = stationId;
        this.userId = userId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
        this.totalSales = totalSales;
        this.expectedCash = expectedCash;
        this.reconciled = reconciled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public Double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Double totalSales) {
        this.totalSales = totalSales;
    }

    public Double getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(Double expectedCash) {
        this.expectedCash = expectedCash;
    }

    public boolean isReconciled() {
        return reconciled;
    }

    public void setReconciled(boolean reconciled) {
        this.reconciled = reconciled;
    }
}
