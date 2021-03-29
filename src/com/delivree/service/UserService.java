package com.delivree.service;

import com.delivree.model.Order;
import com.delivree.model.Product;
import com.delivree.model.User;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private ArrayList<User> users;
    private final OrderService orderService;

    public UserService(OrderService os) {
        orderService = os;
        this.users = new ArrayList<User>();
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public Optional<User> getUserById(UUID userId) {
        return this.users.stream()
                    .filter(u -> u.getUserId() == userId)
                    .findFirst();
    }

    public void addProductToUserCart(Product prod, UUID userId) {
        var userOpt = this.getUserById(userId);
        userOpt.ifPresentOrElse(
                user -> user.getCart().add(prod),
                () -> System.out.println("User not found!"));
    }

    public void showCart(UUID userId) {
        var userOpt = this.getUserById(userId);
        userOpt.ifPresentOrElse(
                user -> System.out.println(user.printCart()),
                () -> System.out.println("User not found!"));
    }

    public void createOrder(UUID userId) {
        var userOpt = this.getUserById(userId);
        userOpt.ifPresentOrElse(
                user -> {
                    var newOrder = new Order(user.getUserId(), user.getCart());
                    orderService.addOrder(newOrder);
                    user.emptyCart();
                },
                () -> System.out.println("User not found!"));
    }
}
