package com.delivree.service;

import com.delivree.model.Order;
import com.delivree.model.Product;
import com.delivree.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    static final private int MAX_NUM_USERS = 100;
    private User[] users = null;
    private final OrderService orderService;
    private int currentNumUsers = 0;

    public UserService(OrderService os) {
        orderService = os;
    }

    public void addUser(User user) {
        if(this.users == null) {
            this.users = new User[]{user};
        }
        else {
            if(this.users.length == MAX_NUM_USERS) {
                System.out.println("Maximum user capacity reached.");
                return;
            }
            this.users = Arrays.copyOf(this.users, this.users.length+1);
            this.users[this.users.length-1] = user;
        }
    }

    public Optional<User> getUserById(UUID userId) {
        User user = null;
        for (int i = 0; i < this.users.length && user == null; i++) {
            if(this.users[i].getUserId() == userId) {
                user = this.users[i];
            }
        }
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
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

    public void listUsers() {
        Arrays.sort(this.users);
        for (int i = 0; i < this.users.length; i++) {
            System.out.println(this.users[i].toString());
        }
    }
}
