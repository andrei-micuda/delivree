package com.delivree.service;

import com.delivree.model.Address;
import com.delivree.utils.DbLayer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressService {
    private static AddressService instance;

    public static AddressService getInstance() {
        if (instance == null) {
            instance = new AddressService();
        }
        return instance;
    }

    public final Connection _db;
    public AddressService() {
        _db = DbLayer.getInstance().getConnection();
    }
    public void insert(Address add) {
        try{
            String sql = "INSERT INTO addresses (street, city, number) VALUES (?, ?, ?)";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, add.getStreet());
            stmt.setString(2, add.getCity());
            stmt.setInt(3, add.getNumber());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public Integer getId(Address add) {
        try{
            String sql = "SELECT * FROM addresses WHERE street = ? AND city = ? AND number = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, add.getStreet());
            stmt.setString(2, add.getCity());
            stmt.setInt(3, add.getNumber());
            var rs = stmt.executeQuery();

            rs.next();
            return rs.getInt("address_id");

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

    public Address getById(int addId) {
        try{
            String sql = "SELECT * FROM addresses WHERE address_id = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setInt(1, addId);
            var rs = stmt.executeQuery();

            rs.next();
            return  new Address(rs.getString("street"), rs.getInt("number"), rs.getString("city"));

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }
}
