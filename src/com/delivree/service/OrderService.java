package com.delivree.service;

import com.delivree.model.Order;
import com.delivree.model.OrderStatus;
import com.delivree.model.Restaurant;
import com.delivree.model.User;
import com.delivree.utils.CsvReadWrite;

import java.util.*;
import java.util.stream.Collectors;

public class OrderService {
    private static OrderService instance;

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

    public Optional<Order> getOrderById(UUID orderId) {
        return this.orders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst();
    }

    public void showOrders() {
        System.out.println("ORDERS");
        for(var o : this.orders) {
            System.out.println(o.toString());
        }
    }

    public List<Order> getUncompletedOrdersByUserId(UUID userId){
        return orders.stream()
                .filter(o ->
                        o.getUserId().equals(userId) &&
                        (o.getStatus().equals(OrderStatus.Placed) ||o.getStatus().equals(OrderStatus.Delivering)))
                .collect(Collectors.toList());
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
        var orderOpt = this.getOrderById(orderId);
        orderOpt.ifPresentOrElse(
                order -> order.setStatus(OrderStatus.Cancelled),
                () -> System.out.println("Order not found!")
        );
    }

    public void assignDriverToOrder(UUID orderId, UUID driverId) {
        var orderOpt = this.getOrderById(orderId);
        orderOpt.ifPresentOrElse(
                order -> {
                    order.setDriverId(driverId);
                    order.setStatus(OrderStatus.Delivering);
                },
                () -> System.out.println("Order not found!")
        );
    }

    public void completeOrder(UUID orderId) {
        var orderOpt = this.getOrderById(orderId);
        orderOpt.ifPresentOrElse(
                order -> {
                    order.setStatus(OrderStatus.Completed);
                    driverService.increaseCompletedDeliveries(order.getDriverId());
                },
                () -> System.out.println("Order not found!")
        );
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
}
