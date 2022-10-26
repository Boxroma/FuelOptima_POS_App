package io.grindallday.endrone_mobile_app.model;

public class Product {

    private String product_id;
    private String name;
    private String description;
    private double price;
    private String type;
    private String station_id;
    private String pump_no;
    private double quantity;
    private boolean active;

    public Product (String product_id,
                    String name,
                    String description,
                    double price ,
                    String type,
                    String station_id,
                    String pump_no,
                    double quantity,
                    boolean active){
        this.product_id = product_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.station_id = station_id;
        this.pump_no = pump_no;
        this.quantity = quantity; //Depending on the context its been used.
        this.active = active;
    }

    public Product(String name) {
        this.name = name;
    }

    public Product() {

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

    public double getPrice() {
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getPump_no() {
        return pump_no;
    }

    public void setPump_no(String pump_no) {
        this.pump_no = pump_no;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
