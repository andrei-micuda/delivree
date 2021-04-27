package com.delivree.model;

import com.delivree.utils.ICsvConvertible;

import java.util.ArrayList;
import java.util.UUID;

public class Driver extends Person implements ICsvConvertible<Driver> {
    private final UUID driverId;
    protected Vehicle vehicle;
    protected DriverStatus status;
    protected int completedDeliveries = 0;

    public Driver(UUID driverId, String firstName, String lastName, int age, Vehicle vehicle, DriverStatus status, int completedDeliveries) {
        super(firstName, lastName, age);
        this.driverId = driverId;
        this.vehicle = vehicle;
        this.status = status;
        this.completedDeliveries = completedDeliveries;
    }

    public Driver(String firstName, String lastName, int age, Vehicle vehicle) {
        super(firstName, lastName, age);
        this.driverId = UUID.randomUUID();
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

    @Override
    public String[] stringify() {
        // driverId, firstName, lastName, age, vehicle, status, completedOrders
        ArrayList s = new ArrayList<String>();
        s.add(this.driverId.toString());
        s.add(this.firstName);
        s.add(this.lastName);
        s.add(Integer.toString(this.age));
        s.add(this.vehicle.toString());
        s.add(this.status.toString());
        s.add(Integer.toString(this.completedDeliveries));
        return (String[])s.toArray(new String[0]);
    }

    public static Driver parse(String csv) {
        var parts = csv.split(",");
        UUID driverId = UUID.fromString(parts[0]);
        String firstName = parts[1];
        String lastName = parts[2];
        int age = Integer.parseInt(parts[3]);
        Vehicle vehicle = Vehicle.valueOf(parts[4]);
        DriverStatus status = DriverStatus.valueOf(parts[5]);
        int completedOrders = Integer.parseInt(parts[6]);

        return new Driver(driverId, firstName, lastName, age, vehicle, status, completedOrders);
    }
}
