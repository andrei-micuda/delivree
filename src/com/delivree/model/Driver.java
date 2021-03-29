package com.delivree.model;

import java.util.UUID;

public class Driver extends Person {
    private final UUID driverId = UUID.randomUUID();
    protected Vehicle vehicle;
    protected DriverStatus status;
    protected int completedDeliveries = 0;

    public Driver(String firstName, String lastName, int age, Vehicle vehicle) {
        super(firstName, lastName, age);
        this.vehicle = vehicle;
        this.status = DriverStatus.Available;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public int getCompletedDeliveries() {
        return completedDeliveries;
    }

    public void setCompletedDeliveries(int completedDeliveries) {
        this.completedDeliveries = completedDeliveries;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + ", Completed deliveries: " + this.completedDeliveries;
    }
}
