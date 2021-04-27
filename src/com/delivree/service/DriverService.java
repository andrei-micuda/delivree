package com.delivree.service;

import com.delivree.model.Driver;
import com.delivree.model.DriverStatus;
import com.delivree.model.Restaurant;
import com.delivree.model.User;
import com.delivree.utils.CsvReadWrite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DriverService {
    private static DriverService instance;

    public static DriverService getInstance() {
        if (instance == null) {
            instance = new DriverService();
        }
        return instance;
    }

    private ArrayList<Driver> drivers;

    private DriverService() {
        this.drivers = new ArrayList<Driver>();
    }

    public Optional<Driver> getDriverById(UUID driverId) {
        return this.drivers.stream()
                .filter(r -> r.getDriverId().equals(driverId))
                .findFirst();
    }

    public void addDriver(Driver driver) {
        this.drivers.add(driver);
    }

    public void listDrivers() {
        for(var d : drivers) {
            System.out.println("DRIVERS");
            System.out.println(d.toString());
        }
    }

    public UUID getFirstAvailableDriver() {
        return this.drivers.stream()
                .filter(d -> d.getStatus().equals(DriverStatus.Available))
                .map(d -> d.getDriverId())
                .findFirst()
                .orElse(null);
    }

    public void increaseCompletedDeliveries(UUID driverId) throws Exception {
        var driverOpt = this.getDriverById(driverId);
        var driver = driverOpt.orElseThrow(() -> new Exception("Driver not found"));
        driver.setCompletedDeliveries(driver.getCompletedDeliveries() + 1);
    }

    public void showDrivers() {
        for(var d : this.drivers) {
            System.out.println("DRIVERS");
            System.out.println(d.toString());
        }
    }

    public void saveAll(String file_path) {
        if(this.drivers == null) return;
        CsvReadWrite.writeAll(this.drivers, file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> Driver.parse(csv))
                    .collect(Collectors.toList());
            this.drivers = new ArrayList(lst);
        });

    }
}
