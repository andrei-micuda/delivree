package com.delivree.service;

import com.delivree.model.Product;
import com.delivree.model.Restaurant;
import com.delivree.model.Review;
import com.delivree.utils.CsvReadWrite;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestaurantService {
    private static RestaurantService instance;

    public static RestaurantService getInstance() {
        if (instance == null) {
            instance = new RestaurantService();
        }
        return instance;
    }

    private ProductService ps;
    private ReviewService revS;
    private ArrayList<Restaurant> restaurants;

    private RestaurantService() {
        this.ps = ProductService.getInstance();
        this.revS = ReviewService.getInstance();
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
        return this.restaurants.stream()
                .filter(r -> r.getName().equals(restName))
                .map(r -> r.getRestaurantId())
                .findFirst();
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

    public ArrayList<String> restaurantsOverview() {
        var res = new ArrayList<String>();
        for(var r : this.restaurants) {
            res.add(r.getName());
        }
        return res;
    }

    public void listRestaurants() {
        for(var r : restaurants) {
            System.out.println("RESTAURANTS");
            System.out.println(r.toString());
        }
    }

    public void showMenu(UUID restId) {
        System.out.println("MENU:");
        var menuOpt = this.getMenuByRestaurantId(restId);
        menuOpt.ifPresentOrElse(
                menu -> {
                    for(var prod : menu) {
                        System.out.println(prod.toString());
                    }
                },
                () -> System.out.println("Restaurant not found!"));
    }

    public void addProductToRestaurant(Product prod, UUID restId) {
        var restOpt = this.getRestaurantById(restId);
        restOpt.ifPresentOrElse(
                rest -> rest.getProducts().add(prod.getProductId()),
                () -> System.out.println("Restaurant not found!"));
    }

    public void addReviewToRestaurant(Review rev) {
        var restOpt = this.getRestaurantById(rev.getRestaurantId());
        restOpt.ifPresentOrElse(
                rest -> {
                    revS.addReview(rev);
                    rest.addReview(rev.getUserId());
                },
                () -> System.out.println("Restaurant not found!"));
    }

    public void showRestaurantReviews(UUID restId) {
        var restOpt = this.getRestaurantById(restId);
        restOpt.ifPresentOrElse(
                rest -> {
                    System.out.println(rest.getName() + " REVIEWS");
                    for(var rev : rest.getReviews()) {
                        System.out.println(rev.toString());
                    }
                },
                () -> System.out.println("Restaurant not found!"));
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
}
