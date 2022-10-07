package io.grindallday.endrone_mobile_app.model;

import com.google.firebase.Timestamp;

public class Station {
    private String address;
    private Timestamp created;
    private String dieselTankSize;
    private String petrolTankSize;
    private String keroseneTankSize;
    private String noDieselPumps;
    private String noPetrolPumps;
    private String noKerosenePumps;
    private String name;

    public Station(String address, Timestamp created, String dieselTankSize, String petrolTankSize, String keroseneTankSize, String noDieselPumps, String noPetrolPumps, String noKerosenePumps, String name) {
        this.address = address;
        this.created = created;
        this.dieselTankSize = dieselTankSize;
        this.petrolTankSize = petrolTankSize;
        this.keroseneTankSize = keroseneTankSize;
        this.noDieselPumps = noDieselPumps;
        this.noPetrolPumps = noPetrolPumps;
        this.noKerosenePumps = noKerosenePumps;
        this.name = name;
    }

    public Station() {

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

    public String getDieselTankSize() {
        return dieselTankSize;
    }

    public void setDieselTankSize(String dieselTankSize) {
        this.dieselTankSize = dieselTankSize;
    }

    public String getPetrolTankSize() {
        return petrolTankSize;
    }

    public void setPetrolTankSize(String petrolTankSize) {
        this.petrolTankSize = petrolTankSize;
    }

    public String getKeroseneTankSize() {
        return keroseneTankSize;
    }

    public void setKeroseneTankSize(String keroseneTankSize) {
        this.keroseneTankSize = keroseneTankSize;
    }

    public String getNoDieselPumps() {
        return noDieselPumps;
    }

    public void setNoDieselPumps(String noDieselPumps) {
        this.noDieselPumps = noDieselPumps;
    }

    public String getNoPetrolPumps() {
        return noPetrolPumps;
    }

    public void setNoPetrolPumps(String noPetrolPumps) {
        this.noPetrolPumps = noPetrolPumps;
    }

    public String getNoKerosenePumps() {
        return noKerosenePumps;
    }

    public void setNoKerosenePumps(String noKerosenePumps) {
        this.noKerosenePumps = noKerosenePumps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
