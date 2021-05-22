package com.delivree.service;

import com.delivree.model.*;
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
import java.util.stream.Collectors;

public class ProductService {
    private static ProductService instance;
    private final Connection _db;

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    private ArrayList<Product> products;

    private ProductService() {
        _db = DbLayer.getInstance().getConnection();
        products = new ArrayList<Product>();
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void listProducts() {
//        for(var p : this.products) {
//            System.out.println(p.toString());
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "BIN_TO_UUID(restaurant_id) AS restaurant_id,\n" +
                    "BIN_TO_UUID(product_id) AS product_id,\n" +
                    "name,\n" +
                    "price,\n" +
                    "ingredients\n" +
                    "FROM products;");
            while(rs.next()) {

                var p = new Product(
                        UUID.fromString(rs.getString("restaurant_id")),
                        UUID.fromString(rs.getString("product_id")),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("ingredients")
                        );

                System.out.println(p.toString());
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public ArrayList<Pair<UUID, String>> overviewProducts() {
        var res = new ArrayList<Pair<UUID, String>>();
//        for (var p : products) {
//            res.add(new Pair(p.getProductId(), p.getName()));
//        }
        try{
            var stmt = _db.createStatement();
            var rs = stmt.executeQuery("SELECT\n" +
                    "BIN_TO_UUID(restaurant_id) AS restaurant_id,\n" +
                    "BIN_TO_UUID(product_id) AS product_id,\n" +
                    "name,\n" +
                    "price,\n" +
                    "ingredients\n" +
                    "FROM products;");
            while(rs.next()) {

                var p = new Product(
                        UUID.fromString(rs.getString("restaurant_id")),
                        UUID.fromString(rs.getString("product_id")),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("ingredients")
                );

                res.add(new Pair(p.getProductId(), p.getName()));
            }

        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
        return res;
    }

    public Product parseResultSetItem(ResultSet rs){
        try{
            var restId = UUID.fromString(rs.getString("restaurant_id"));
            var prodId = UUID.fromString(rs.getString("product_id"));
            var name = rs.getString("name");
            var price = rs.getFloat("price");
            var ingredients = rs.getString("ingredients");
            return new Product(restId, prodId, name, price, ingredients);
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public Optional<Product> getProductById(UUID productId) {
//        return this.products.stream()
//                .filter(p -> p.getProductId().equals(productId))
//                .findFirst();
        try{
            String sql = "SELECT\n" +
                    "    BIN_TO_UUID(product_id) AS product_id,\n" +
                    "    name,\n" +
                    "    price,\n" +
                    "    ingredients,\n" +
                    "    BIN_TO_UUID(restaurant_id) AS restaurant_id\n" +
                    "FROM products WHERE product_id = UUID_TO_BIN(?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, productId.toString());
            var rs = stmt.executeQuery();

            rs.next();

            return  Optional.of(parseResultSetItem(rs));

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return Optional.empty();
    }

    public void saveAll(String file_path) {
        if(this.products == null) return;
        CsvReadWrite.writeAll(this.products, file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> Product.parse(csv))
                    .collect(Collectors.toList());
            this.products = new ArrayList(lst);
        });
    }

    public void insert(Product prod) {
        try{
            String sql = "INSERT INTO products\n" +
                    "VALUES (UUID_TO_BIN(?), ?, ?, ?, UUID_TO_BIN(?));";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, prod.getProductId().toString());
            stmt.setString(2, prod.getName());
            stmt.setFloat(3,prod.getPrice());
            stmt.setString(4, prod.printIngredients());
            stmt.setString(5, prod.getRestaurantId().toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
