package com.delivree.service;

import com.delivree.model.Order;
import com.delivree.model.Product;
import com.delivree.model.User;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserService {
    private static UserService instance;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    static final private int MAX_NUM_USERS = 100;
    private User[] users = null;
    private final OrderService orderService;
    private final ProductService productService;
    private int currentNumUsers = 0;

    private UserService() {
        orderService = OrderService.getInstance();
        productService = ProductService.getInstance();
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
            if(this.users[i].getUserId().equals(userId)) {
                user = this.users[i];
            }
        }
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public ArrayList<Pair<UUID, String>> overviewUsers() {
        var res = new ArrayList<Pair<UUID, String>>();
        for (int i = 0; i < this.users.length; i++) {
            res.add(new Pair(this.users[i].getUserId(), this.users[i].toString()));
        }
        return res;
    }


    public void addProductToUserCart(UUID prodId, UUID userId) {
        var userOpt = this.getUserById(userId);
        userOpt.ifPresentOrElse(
                user -> user.addToCart(prodId),
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
        System.out.println("USERS");
        for (int i = 0; i < this.users.length; i++) {
            System.out.println(this.users[i].toString());
        }
    }

    public void saveAll(String file_path) {
        if(this.users == null) return;
        CsvReadWrite.writeAll(new ArrayList(Arrays.asList(this.users)), file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> User.parse(csv))
                    .collect(Collectors.toList());
            this.users = (User[])new ArrayList(lst).toArray(new User[0]);
        });
    }
}
