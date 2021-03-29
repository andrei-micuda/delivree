package com.delivree.model;

import java.util.ArrayList;
import java.util.UUID;

public class Restaurant {
    private final UUID restaurantId = UUID.randomUUID();
    protected String name;
    protected ArrayList<Product> products;
    protected ArrayList<Review> reviews;
    protected Address location;
    protected String description;

    public Restaurant(String name, String description, Address location) {
        this.name = name;
        this.description = description;
        this.products = new ArrayList<Product>();
        this.reviews = new ArrayList<Review>();
        this.location = location;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Restaurant " + this.name +
                "\nDescription: " + this.description +
                "\nAddress: " + this.location.toString();
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
