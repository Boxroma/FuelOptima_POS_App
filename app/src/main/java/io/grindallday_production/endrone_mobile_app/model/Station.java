package io.grindallday_production.endrone_mobile_app.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Station {

    private String id;
    private String name;
    private String address;
    private Timestamp created;
    private String shiftId;
    private Double dieselTankSize;
    private Double petrolTankSize;
    private Double keroseneTankSize;
    private ArrayList<String> dieselPumpIds;
    private ArrayList<String> petrolPumpIds;
    private ArrayList<String> kerosenePumpIds;

    public Station(String address, Timestamp created, Double dieselTankSize, Double petrolTankSize, Double keroseneTankSize, ArrayList<String> dieselPumps, ArrayList<String> petrolPumps, ArrayList<String> kerosenePumps, String name) {
        this.address = address;
        this.created = created;
        this.dieselTankSize = dieselTankSize;
        this.petrolTankSize = petrolTankSize;
        this.keroseneTankSize = keroseneTankSize;
        this.dieselPumpIds = dieselPumps;
        this.petrolPumpIds = petrolPumps;
        this.kerosenePumpIds = kerosenePumps;
        this.name = name;
    }

    public Station() {

    }

    public Station(String id, String address, Timestamp created,String shiftId, Double dieselTankSize, Double petrolTankSize, Double keroseneTankSize, Object dieselPumps, Object petrolPumps, Object kerosenePumps, String name) {
        this.id = id;
        this.address = address;
        this.created = created;
        this.shiftId = shiftId;
        this.dieselTankSize = dieselTankSize;
        this.petrolTankSize = petrolTankSize;
        this.keroseneTankSize = keroseneTankSize;
        this.dieselPumpIds = (ArrayList<String>) dieselPumps;
        this.petrolPumpIds = (ArrayList<String>) petrolPumps;
        this.kerosenePumpIds = (ArrayList<String>) kerosenePumps;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Double getDieselTankSize() {
        return dieselTankSize;
    }

    public void setDieselTankSize(Double dieselTankSize) {
        this.dieselTankSize = dieselTankSize;
    }

    public Double getPetrolTankSize() {
        return petrolTankSize;
    }

    public void setPetrolTankSize(Double petrolTankSize) {
        this.petrolTankSize = petrolTankSize;
    }

    public Double getKeroseneTankSize() {
        return keroseneTankSize;
    }

    public void setKeroseneTankSize(Double keroseneTankSize) {
        this.keroseneTankSize = keroseneTankSize;
    }

    public ArrayList<String> getDieselPumpIds() {
        return dieselPumpIds;
    }

    public void setDieselPumpIds(ArrayList<String> dieselPumpIds) {
        this.dieselPumpIds = dieselPumpIds;
    }

    public ArrayList<String> getPetrolPumpIds() {
        return petrolPumpIds;
    }

    public void setPetrolPumpIds(ArrayList<String> petrolPumpIds) {
        this.petrolPumpIds = petrolPumpIds;
    }

    public ArrayList<String> getKerosenePumpIds() {
        return kerosenePumpIds;
    }

    public void setKerosenePumpIds(ArrayList<String> kerosenePumpIds) {
        this.kerosenePumpIds = kerosenePumpIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShiftId() {
        return shiftId;
    }
}
