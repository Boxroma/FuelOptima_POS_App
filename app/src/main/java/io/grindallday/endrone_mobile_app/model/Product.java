package io.grindallday.endrone_mobile_app.model;

public class Product {

    private String name;
    private String description;
    private int price;
    private String type;
    private String station_id;
    private int quantity;

    public Product (String name, String description, int price , String type, String station_id, int quantity){
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.station_id = station_id;
        this.quantity = quantity;
    }

    public Product(String name) {
        this.name = name;
    }

    public Product() {
        this.name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
