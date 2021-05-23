package com.delivree.service;

import com.delivree.model.*;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.DbLayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DriverService {
    private static DriverService instance;
    private Connection _db =DbLayer.getInstance().getConnection();

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
        System.out.println("DRIVERS");

//        for(var d : drivers) {
//            System.out.println(d.toString());
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "    BIN_TO_UUID(driver_id) AS driver_id,\n" +
                    "    first_name,\n" +
                    "       last_name,\n" +
                    "       age,\n" +
                    "       vehicle,\n" +
                    "       status,\n" +
                    "       completed_deliveries\n" +
                    "FROM drivers;");
            while(rs.next()) {

                var d = new Driver(
                        UUID.fromString(rs.getString("driver_id")),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        Vehicle.valueOf(rs.getString("vehicle")),
                        DriverStatus.valueOf(rs.getString("status")),
                        rs.getInt("completed_deliveries"));

                System.out.println(d.toString());
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public UUID getFirstAvailableDriver() {
//        return this.drivers.stream()
//                .filter(d -> d.getStatus().equals(DriverStatus.Available))
//                .map(d -> d.getDriverId())
//                .findFirst()
//                .orElse(null);
        try{
            var sql = "SELECT BIN_TO_UUID(driver_id) AS driver_id\n" +
                    "FROM drivers WHERE status = 'Available'\n" +
                    "LIMIT 1;";
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery(sql);
            rs.next();
            return UUID.fromString(rs.getString("driver_id"));
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public void increaseCompletedDeliveries(UUID driverId) throws Exception {
//        var driverOpt = this.getDriverById(driverId);
//        var driver = driverOpt.orElseThrow(() -> new Exception("Driver not found"));
//        driver.setCompletedDeliveries(driver.getCompletedDeliveries() + 1);
        try{
            String sql = "UPDATE drivers\n" +
                    "SET completed_deliveries = completed_deliveries + 1\n" +
                    "WHERE BIN_TO_UUID(driver_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, driverId.toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
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

    public void insert(Driver driver) {
        try{
            String sql = "INSERT INTO drivers\n" +
                    "VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?, ?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, driver.getDriverId().toString());
            stmt.setString(2, driver.getFirstName());
            stmt.setString(3, driver.getLastName());
            stmt.setInt(4, driver.getAge());
            stmt.setString(5, driver.getVehicle().toString());
            stmt.setString(6, driver.getStatus().toString());
            stmt.setInt(7, driver.getCompletedDeliveries());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
