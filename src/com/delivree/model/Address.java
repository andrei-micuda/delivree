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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
