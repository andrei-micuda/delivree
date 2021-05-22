package com.delivree.service;

import com.delivree.model.Address;
import com.delivree.model.Order;
import com.delivree.model.Product;
import com.delivree.model.User;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.DbLayer;
import com.delivree.utils.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserService {
    private static UserService instance;
    private static final AddressService as = AddressService.getInstance();

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public final Connection _db;
    static final private int MAX_NUM_USERS = 100;
    private User[] users = null;
    private final OrderService orderService;
    private final ProductService productService;
    private int currentNumUsers = 0;

    private UserService() {
        _db = DbLayer.getInstance().getConnection();
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
//        for (int i = 0; i < this.users.length; i++) {
//            res.add(new Pair(this.users[i].getUserId(), this.users[i].toString()));
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "    BIN_TO_UUID(user_id) AS user_id,\n" +
                    "    first_name,\n" +
                    "    last_name,\n" +
                    "    age,\n" +
                    "    address_id\n" +
                    "FROM users;");
            while(rs.next()) {

                var u = new User(
                        UUID.fromString(rs.getString("user_id")),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getInt("age"),
                        rs.getInt("address_id"));

                res.add(new Pair(u.getUserId(), u.toString()));
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return res;
    }


    public void addProductToUserCart(UUID prodId, UUID userId) throws Exception {
//        var userOpt = this.getUserById(userId);
//        var user = userOpt.orElseThrow(() -> new Exception("User not found"));
//        user.addToCart(prodId);
        try{
            String sql = "INSERT INTO users_products\n" +
                    "VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?));";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, userId.toString());
            stmt.setString(2, prodId.toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void showCart(UUID userId) throws Exception {
//        var userOpt = this.getUserById(userId);
//        var user = userOpt.orElseThrow(() -> new Exception("User not found"));
//        System.out.println(user.printCart());
        String res = "";
//        res += "SHOPPING CART:\n";
//        for(var p : this.cart) {
//            res += ps.getProductById(p).get().summary();
//        }
        try{
            var sql = "SELECT\n" +
                    "       BIN_TO_UUID(P.product_id) AS product_id,\n" +
                    "       P.name as name,\n" +
                    "       P.price as price,\n" +
                    "       P.ingredients as ingredients,\n" +
                    "       BIN_TO_UUID(P.restaurant_id) as restaurant_id\n" +
                    "FROM products AS P, users_products UP\n" +
                    "WHERE\n" +
                    "      UP.product_id = P.product_id AND\n" +
                    "      UP.user_id = UUID_TO_BIN(?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, userId.toString());
            var rs = stmt.executeQuery();

            while(rs.next()) {

                var p = new Product(
                        UUID.fromString(rs.getString("restaurant_id")),
                        UUID.fromString(rs.getString("product_id")),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("ingredients"));

                res += p.summary();
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

        System.out.println(res);
    }

    public void createOrder(UUID userId) throws Exception {
        var newOrder = new Order(userId, getCart(userId));
        orderService.insert(newOrder);
        emptyCart(userId);
    }

    public void emptyCart(UUID userId) {
        try{
            String sql = "DELETE\n" +
                    "FROM users_products\n" +
                    "WHERE user_id = UUID_TO_BIN(?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, userId.toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public ArrayList<UUID> getCart(UUID userId){
        ArrayList<UUID> res = new ArrayList<UUID>();
        try{
            var sql = "SELECT\n" +
                    "       BIN_TO_UUID(P.product_id) AS product_id\n" +
                    "FROM products AS P, users_products UP\n" +
                    "WHERE\n" +
                    "      UP.product_id = P.product_id AND\n" +
                    "      UP.user_id = UUID_TO_BIN(?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, userId.toString());
            var rs = stmt.executeQuery();

            while(rs.next()) {
                res.add(UUID.fromString(rs.getString("product_id")));
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return res;
    }

    public void listUsers() {
//        Arrays.sort(this.users);
//        System.out.println("USERS");
//        for (int i = 0; i < this.users.length; i++) {
//            System.out.println(this.users[i].toString());
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "    BIN_TO_UUID(user_id) AS user_id,\n" +
                    "    first_name,\n" +
                    "    last_name,\n" +
                    "    age,\n" +
                    "    address_id\n" +
                    "FROM users;");
            while(rs.next()) {

                var u = new User(
                        UUID.fromString(rs.getString("user_id")),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getInt("age"),
                        rs.getInt("address_id"));

                System.out.println(u.toString());
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
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

    public void insert(User user) {
        try{
            var add = user.getDeliveryAddress();
            as.insert(add);
            var addId = as.getId(add);
            String sql = "INSERT INTO users\n" +
                    "VALUES (UUID_TO_BIN(?), ?, ?, ?, ?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, user.getUserId().toString());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setInt(4, user.getAge());
            stmt.setInt(5, addId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
