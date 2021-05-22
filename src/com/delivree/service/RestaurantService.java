package com.delivree.service;

import com.delivree.model.*;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.DbLayer;
import com.delivree.utils.Pair;

import java.io.OptionalDataException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestaurantService {
    private static RestaurantService instance;
    private final Connection _db;

    public static RestaurantService getInstance() {
        if (instance == null) {
            instance = new RestaurantService();
        }
        return instance;
    }

    private AddressService as;
    private ProductService ps;
    private ReviewService revS;
    private ArrayList<Restaurant> restaurants;

    private RestaurantService() {
        this.ps = ProductService.getInstance();
        this.revS = ReviewService.getInstance();
        this.as = as = AddressService.getInstance();
        this._db = DbLayer.getInstance().getConnection();
        this.restaurants = new ArrayList<Restaurant>();
    }

    public void addRestaurant(Restaurant rest) {
        restaurants.add(rest);
    }

    public Optional<Restaurant> getRestaurantById(UUID restId) {
        return this.restaurants.stream()
                        .filter(r -> r.getRestaurantId().equals(restId))
                        .findFirst();
    }

    public Optional<UUID> getRestaurantIdFromName(String restName) {
//        return this.restaurants.stream()
//                .filter(r -> r.getName().equals(restName))
//                .map(r -> r.getRestaurantId())
//                .findFirst();

        try{
            String sql = "SELECT BIN_TO_UUID(restaurant_id) AS restaurant_id FROM restaurants WHERE name = ?;";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, restName);
            var rs = stmt.executeQuery();

            rs.next();

            return Optional.of(UUID.fromString(rs.getString("restaurant_id")));

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return Optional.empty();
    }

    public Optional<ArrayList<Product>> getMenuByRestaurantId(UUID restId) {
        var prodIds =  this.restaurants.stream()
                .filter(r -> r.getRestaurantId().equals(restId))
                .findFirst()
                .map(Restaurant::getProducts)
                .get();
        var products = prodIds.stream().map(prodId -> ps.getProductById(prodId).get()).collect(Collectors.toList());
        return Optional.of(new ArrayList<Product>(products));
    }

    public ArrayList<Pair<String, UUID>> overviewRestaurants() {
        var res = new ArrayList<Pair<String, UUID>>();
//        for(var r : this.restaurants) {
//            res.add(r.getName());
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "    BIN_TO_UUID(restaurant_id) AS restaurant_id,\n" +
                    "    name,\n" +
                    "    address_id,\n" +
                    "    description\n" +
                    "FROM restaurants;");
            while(rs.next()) {

                var r = new Restaurant(
                        UUID.fromString(rs.getString("restaurant_id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("address_id"));

                res.add(new Pair(r.getName(), r.getRestaurantId()));
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return res;
    }

    public void listRestaurants() {
//        for(var r : restaurants) {
//            System.out.println("RESTAURANTS");
//            System.out.println(r.toString());
//        }

        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "    BIN_TO_UUID(restaurant_id) AS restaurant_id,\n" +
                    "    name,\n" +
                    "    address_id,\n" +
                    "    description\n" +
                    "FROM restaurants;");
            while(rs.next()) {

                var r = new Restaurant(
                        UUID.fromString(rs.getString("restaurant_id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("address_id"));

                System.out.println(r.toString());
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void showMenu(UUID restId) throws Exception {
        System.out.println("MENU:");
//        var menuOpt = this.getMenuByRestaurantId(restId);
        ArrayList<Product> menu = new ArrayList<Product>();

        try{
            String sql = "SELECT\n" +
                    "    BIN_TO_UUID(product_id) AS product_id,\n" +
                    "    name,\n" +
                    "    price,\n" +
                    "    ingredients,\n" +
                    "    BIN_TO_UUID(restaurant_id) AS restaurant_id\n" +
                    "FROM products WHERE restaurant_id = UUID_TO_BIN(?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, restId.toString());
            var rs = stmt.executeQuery();

            while(rs.next())
            {
                var p = ps.parseResultSetItem(rs);
                menu.add(p);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }

        for(var prod : menu) {
            System.out.println(prod.toString());
        }
    }

    public void addProductToRestaurant(Product prod, UUID restId) throws Exception {
        var restOpt = this.getRestaurantById(restId);
        var rest = restOpt.orElseThrow(() -> new Exception("Restaurant not found"));
        rest.getProducts().add(prod.getProductId());
    }

    public void addReviewToRestaurant(Review rev) throws Exception {
        var restOpt = this.getRestaurantById(rev.getRestaurantId());
        var rest = restOpt.orElseThrow(() -> new Exception("Restaurant not found"));

        revS.addReview(rev);
        rest.addReview(rev.getUserId());
    }

    public void showRestaurantReviews(UUID restId) throws Exception {
        var restOpt = this.getRestaurantById(restId);
        var rest = restOpt.orElseThrow(() -> new Exception("Restaurant not found"));

        System.out.println(rest.getName() + " REVIEWS");
        for(var rev : rest.getReviews()) {
            System.out.println(rev.toString());
        }
    }

    public void saveAll(String file_path) {
        if(this.restaurants == null) return;
        CsvReadWrite.writeAll(this.restaurants, file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> Restaurant.parse(csv))
                    .collect(Collectors.toList());
            this.restaurants = new ArrayList(lst);
        });
    }

    public void insert(Restaurant rest) {
        try{
            var add = rest.getLocation();
            as.insert(add);
            var addId = as.getId(add);
            String sql = "INSERT INTO restaurants\n" +
                    "VALUES (UUID_TO_BIN(?), ?, ?, ?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, rest.getRestaurantId().toString());
            stmt.setString(2, rest.getName());
            stmt.setInt(3, addId);
            stmt.setString(4, rest.getDescription());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
