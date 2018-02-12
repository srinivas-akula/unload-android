package com.sringa.unload.db;

import java.io.Serializable;

public class VehicleDetail implements Serializable {

    private long id;
    private String number;
    private String location;
    private String model = "Semi-Bed";
    private int tonnage = 0;
    private int load = 0;
    private String axle;

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getTonnage() {
        return tonnage;
    }

    public void setTonnage(int tonnage) {
        this.tonnage = tonnage;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAxle() {
        return axle;
    }

    public void setAxle(String axle) {
        this.axle = axle;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
