package com.delivree.model;

public class Address {
    protected String street;
    protected int number;
    protected String city;

    public Address(String street, int number, String city) {
        this.street = street;
        this.number = number;
        this.city = city;
    }

    @Override
    public String toString() {
        return this.street + " " + this.number + ", " + this.city;
    }
}
