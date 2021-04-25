package com.delivree.model;

import java.util.ArrayList;
import java.util.UUID;

public class User extends Person implements Comparable<User> {
    private final UUID userId = UUID.randomUUID();
    protected ArrayList<Product> cart;
    Address deliveryAddress;

    public User(String firstName, String lastName, int age, Address deliveryAddress) {
        super(firstName, lastName, age);
        this.deliveryAddress = deliveryAddress;
        cart = new ArrayList<Product>();
    }

    public UUID getUserId() {
        return userId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public ArrayList<Product> getCart() {
        return cart;
    }

    public void emptyCart() { this.cart.clear(); }

    public String printCart() {
        String res = "";
        res += "SHOPPING CART:\n";
        for(var p : this.cart) {
            res += p.toString();
        }
        return res;
    }

    @Override
    public int compareTo(User u) {
        int res = this.getLastName().compareTo(u.getLastName());
        if(res == 0){
            return this.getFirstName().compareTo(u.getFirstName());
        }
        return res;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName;
    }
}
