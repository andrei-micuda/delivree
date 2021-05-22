package com.delivree.utils;
import com.delivree.service.DriverService;

import java.sql.*;

public class DbLayer {
    private Connection _connection = null;
    private static DbLayer instance;

    public static DbLayer getInstance() {
        if (instance == null) {
            instance = new DbLayer();
        }
        return instance;
    }

    public DbLayer() {
        try{
//            Class.forName("com.mysql.jdbc.Driver");
            _connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/delivree_db","root","p@ssw0rd");
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
    }

    public Connection getConnection() {
        return _connection;
    }
}
