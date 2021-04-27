package com.delivree.model;

import com.delivree.utils.ICsvConvertible;
import com.delivree.service.ProductService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

public class Order implements Comparable<Order>, ICsvConvertible<Order> {
    private final UUID orderId;
    protected final UUID userId;
    protected ZonedDateTime timePlaced;
    protected UUID[] products;
    protected OrderStatus status;
    protected UUID driverId;
    protected ProductService ps = ProductService.getInstance();

    public Order(UUID orderId, UUID userId, UUID driverId, ZonedDateTime timePlaced, OrderStatus status, ArrayList<UUID> products) {
        this.orderId = orderId;
        this.userId = userId;
        this.driverId = driverId;
        this.timePlaced = timePlaced;
        this.status = status;
        this.products = new UUID[products.size()];
        for (int i = 0; i < products.size(); i++) {
            this.products[i] = products.get(i);
        }
    }

    public Order(UUID userId, ArrayList<UUID> products) {
        this.orderId = UUID.randomUUID();
        this.userId = userId;
        this.timePlaced = ZonedDateTime.now();
        this.status = OrderStatus.Placed;
        this.products = new UUID[products.size()];
        for (int i = 0; i < products.size(); i++) {
            this.products[i] = products.get(i);
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

    public UUID[] getProducts() {
        return products;
    }

    public void setProducts(UUID[] products) {
        this.products = products;
    }

    @Override
    public String toString() {
        var prodSummary = "";
        for(var prodId : this.products) {
            prodSummary += ps.getProductById(prodId).get().summary();
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

    @Override
    public String[] stringify() {
        // orderId, userId, driverId, timePlaced, status, numberOfProductsInOrder, productId1, ..., productIdn
        ArrayList s = new ArrayList<String>();
        s.add(this.orderId.toString());
        s.add(this.userId.toString());
        if(this.driverId != null)
            s.add(this.driverId.toString());
        else
            s.add("null");
        s.add(this.timePlaced.toString());
        s.add(this.status.toString());
        s.add(Integer.toString(this.products.length));
        for(var p : this.products){
            s.add(p.toString());
        }
        return (String[])s.toArray(new String[0]);
    }

    public static Order parse(String csv) {
        var parts = csv.split(",");
        UUID orderId = UUID.fromString(parts[0]);
        UUID userId = UUID.fromString(parts[1]);
        UUID driverId = null;
        if(!parts[2].equals("null"))
            driverId = UUID.fromString(parts[2]);
        ZonedDateTime timePlaced = ZonedDateTime.parse(parts[3]);
        OrderStatus status = OrderStatus.valueOf(parts[4]);
        var products = new ArrayList<UUID>();
        for (int i = 0; i < Integer.parseInt(parts[5]); i++) {
            products.add(UUID.fromString(parts[6 + i]));
        }
        return new Order(orderId, userId, driverId, timePlaced, status, products);
    }
}
