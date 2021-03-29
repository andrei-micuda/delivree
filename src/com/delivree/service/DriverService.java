package com.delivree.service;

import com.delivree.model.Driver;
import com.delivree.model.DriverStatus;
import com.delivree.model.Restaurant;
import com.delivree.model.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class DriverService {
    private ArrayList<Driver> drivers;

    public DriverService() {
        this.drivers = new ArrayList<Driver>();
    }

    public Optional<Driver> getDriverById(UUID driverId) {
        return this.drivers.stream()
                .filter(r -> r.getDriverId() == driverId)
                .findFirst();
    }

    public void addDriver(Driver driver) {
        this.drivers.add(driver);
    }

    public UUID getFirstAvailableDriver() {
        return this.drivers.stream()
                .filter(d -> d.getStatus() == DriverStatus.Available)
                .map(d -> d.getDriverId())
                .findFirst()
                .orElse(null);
    }

    public void increaseCompletedDeliveries(UUID driverId) {
        var driverOpt = this.getDriverById(driverId);
        driverOpt.ifPresentOrElse(
                driver -> driver.setCompletedDeliveries(driver.getCompletedDeliveries() + 1),
                () -> System.out.println("Driver not found!")
        );
    }

    public void showDrivers() {
        for(var d : this.drivers) {
            System.out.println("DRIVERS");
            System.out.println(d.toString());
        }
    }
}
