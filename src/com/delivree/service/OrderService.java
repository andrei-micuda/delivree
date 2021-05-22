package com.delivree.service;

import com.delivree.model.*;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.DbLayer;
import com.delivree.utils.Pair;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class OrderService {
    private static OrderService instance;
    private Connection _db = DbLayer.getInstance().getConnection();

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }

    private ArrayList<Order> orders;
    private final DriverService driverService;

    private OrderService() {
        orders = new ArrayList<Order>();
        driverService = DriverService.getInstance();
    }

    public String getOrderStatus(UUID orderId) {
//        return this.orders.stream()
//                .filter(o -> o.getOrderId().equals(orderId))
//                .map(o -> o.getStatus())
//                .findFirst()
//                .get()
//                .toString();
        try{
            String sql = "SELECT\n" +
                    "    status\n" +
                    "FROM orders\n" +
                    "WHERE BIN_TO_UUID(order_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, orderId.toString());
            var rs = stmt.executeQuery();

            rs.next();
            return rs.getString("status");

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

    public Optional<Order> getOrderById(UUID orderId) {
//        return this.orders.stream()
//                .filter(o -> o.getOrderId().equals(orderId))
//                .findFirst();
        try{
            var sql = "SELECT\n" +
                    "    BIN_TO_UUID(order_id) AS order_id,\n" +
                    "    BIN_TO_UUID(user_id) AS user_id,\n" +
                    "    BIN_TO_UUID(driver_id) as driver_id,\n" +
                    "    time_placed,\n" +
                    "    status\n" +
                    "FROM orders WHERE UUID_TO_BIN(order_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, orderId.toString());
            var rs = stmt.executeQuery();
            rs.next();
            var o = parseResultSetItem(rs);
            return Optional.of(o);

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return Optional.empty();
    }

    public Order parseResultSetItem(ResultSet rs) {
        try {
            var orderId = UUID.fromString(rs.getString("order_id"));
            var userId = UUID.fromString(rs.getString("user_id"));
            var driverIdStr = rs.getString("driver_id");
            UUID driverId = null;
            if (driverIdStr != null) {
                driverId = UUID.fromString(driverIdStr);
            }
            var timePlaced = rs.getTimestamp("time_placed");
            var status = OrderStatus.valueOf(rs.getString("status"));
            var products = new ArrayList<UUID>();

            String sql2 = "SELECT\n" +
                    "    BIN_TO_UUID(P.product_id) AS product_id\n" +
                    "FROM products P, orders_products OP, orders O\n" +
                    "WHERE\n" +
                    "        O.order_id = UUID_TO_BIN(?) AND\n" +
                    "        P.product_id = OP.product_id AND\n" +
                    "        O.order_id = OP.order_id;";
            var stmt2 = _db.prepareStatement(sql2);
            stmt2.setString(1, orderId.toString());
            var rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                var prodId = UUID.fromString(rs2.getString("product_id"));
                products.add(prodId);
            }

            return new Order(
                    orderId,
                    userId,
                    driverId,
                    ZonedDateTime.ofInstant(timePlaced.toInstant(), ZoneId.of("UTC")),
                    status,
                    products
            );
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public void showOrders() {
        System.out.println("ORDERS");
//        for(var o : this.orders) {
//            System.out.println(o.toString());
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "    BIN_TO_UUID(order_id) AS order_id,\n" +
                    "    BIN_TO_UUID(user_id) AS user_id,\n" +
                    "    BIN_TO_UUID(driver_id) as driver_id,\n" +
                    "    time_placed,\n" +
                    "    status\n" +
                    "FROM orders;");
            while(rs.next()) {
                var o = parseResultSetItem(rs);

                System.out.println(o.toString());
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public ArrayList<Pair<UUID, String>> overviewOrdersByUserId(UUID userId){
        ArrayList<Pair<UUID, String>> res = new ArrayList<Pair<UUID, String>>();
        try{
            String sql = "SELECT\n" +
                    "    BIN_TO_UUID(order_id) AS order_id,\n" +
                    "    BIN_TO_UUID(user_id) AS user_id,\n" +
                    "    BIN_TO_UUID(driver_id) as driver_id,\n" +
                    "    time_placed,\n" +
                    "    status\n" +
                    "FROM orders\n" +
                    "WHERE BIN_TO_UUID(user_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, userId.toString());
            var rs = stmt.executeQuery();

            while(rs.next()) {
                var o = parseResultSetItem(rs);
                res.add(new Pair<UUID, String>(o.getOrderId(), o.toString()));
            }

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }

    public ArrayList<Pair<UUID, String>> overviewUncompletedOrdersByUserId(UUID userId){
//        var lst =  orders.stream()
//                .filter(o ->
//                        o.getUserId().equals(userId) &&
//                        (o.getStatus().equals(OrderStatus.Placed) ||o.getStatus().equals(OrderStatus.Delivering)))
//                .map(o -> new Pair<UUID, String>(o.getOrderId(), o.toString()))
//                .collect(Collectors.toList());
//        return new ArrayList<>(lst);
        ArrayList<Pair<UUID, String>> res = new ArrayList<Pair<UUID, String>>();
        try{
            String sql = "SELECT\n" +
                    "    BIN_TO_UUID(order_id) AS order_id,\n" +
                    "    BIN_TO_UUID(user_id) AS user_id,\n" +
                    "    BIN_TO_UUID(driver_id) as driver_id,\n" +
                    "    time_placed,\n" +
                    "    status\n" +
                    "FROM orders\n" +
                    "WHERE status = 'Placed' OR status = 'Delivering' AND BIN_TO_UUID(user_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, userId.toString());
            var rs = stmt.executeQuery();

            while(rs.next()) {
                var o = parseResultSetItem(rs);
                res.add(new Pair<UUID, String>(o.getOrderId(), o.toString()));
            }

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return res;
    }

    public void showUserOrderHistory(UUID userId) {
        var userOrders = orders.stream()
                        .filter(o -> o.getUserId().equals(userId))
                        .collect(Collectors.toList());
        Collections.sort(userOrders);
        System.out.println("USER ORDER HISTORY");
        for(var o : userOrders) {
            System.out.println(o.toString());
        }
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void cancelOrder(UUID orderId) {
//        var orderOpt = this.getOrderById(orderId);
//        orderOpt.ifPresentOrElse(
//                order -> order.setStatus(OrderStatus.Cancelled),
//                () -> System.out.println("Order not found!")
//        );
        try{
            String sql = "UPDATE orders\n" +
                    "SET status = 'Cancelled'\n" +
                    "WHERE BIN_TO_UUID(order_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, orderId.toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void assignDriverToOrder(UUID orderId, UUID driverId) throws Exception {
        var orderOpt = this.getOrderById(orderId);
        var order = orderOpt.orElseThrow(() -> new Exception("Order not found"));

        order.setDriverId(driverId);
        order.setStatus(OrderStatus.Delivering);
    }

    public void completeOrder(UUID orderId) throws Exception {
//        var orderOpt = this.getOrderById(orderId);
//        var order = orderOpt.orElseThrow(() -> new Exception("Order not found"));
//
//        order.setStatus(OrderStatus.Completed);
//        try {
//            driverService.increaseCompletedDeliveries(order.getDriverId());
//        }
//        catch(Exception ex) {
//            System.out.println(ex);
//        }
        try{
            String sql = "UPDATE orders\n" +
                    "SET status = 'Completed'\n" +
                    "WHERE BIN_TO_UUID(order_id) = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, orderId.toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void saveAll(String file_path) {
        if(this.orders == null) return;
        CsvReadWrite.writeAll(this.orders, file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> Order.parse(csv))
                    .collect(Collectors.toList());
            this.orders = new ArrayList(lst);
        });
    }

    public void insert(Order order) {
        try{
            String sql = "INSERT INTO orders\n" +
                    "VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, order.getOrderId().toString());
            stmt.setString(2, order.getUserId().toString());
            if(order.getDriverId() != null) {
                stmt.setString(3, order.getDriverId().toString());
            }
            else{
                stmt.setNull(3, Types.NULL);
            }
            stmt.setTimestamp(4, Timestamp.from(order.getTimePlaced().toInstant()));
            stmt.setString(5, order.getStatus().toString());
            stmt.executeUpdate();

            for(var prod : order.getProducts()) {
                String sql2 = "INSERT INTO orders_products\n" +
                        "VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?));";
                var stmt2 = _db.prepareStatement(sql2);
                stmt2.setString(1, order.getOrderId().toString());
                stmt2.setString(2, prod.toString());
                stmt2.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
