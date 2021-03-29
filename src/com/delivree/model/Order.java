package com.delivree.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Order implements Comparable<Order> {
    private final UUID orderId = UUID.randomUUID();
    protected final UUID userId;
    protected ZonedDateTime timePlaced;
    protected Product[] products;
    protected OrderStatus status;
    protected UUID driverId;

    public Order(UUID userId, ArrayList<Product> products) {
        this.userId = userId;
        this.timePlaced = ZonedDateTime.now();
        this.status = OrderStatus.Placed;
        this.products = new Product[products.size()];
        for (int i = 0; i < products.size(); i++) {
            this.products[i] = new Product(products.get(i));
        }
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public ZonedDateTime getTimePlaced() {
        return timePlaced;
    }

    public void setTimePlaced(ZonedDateTime timePlaced) {
        this.timePlaced = timePlaced;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    @Override
    public String toString() {
        var prodSummary = "";
        for(var p : this.products) {
            prodSummary += p.summary();
        }

        return "Order #" + this.orderId +
                "\nTime placed: " + DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm").format(this.timePlaced) +
                "\nStatus: " + this.status +
                "\nItems: " + prodSummary;
    }

    @Override
    public int compareTo(Order other) {
        return this.timePlaced.compareTo(other.timePlaced);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }
}
