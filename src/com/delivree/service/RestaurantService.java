package com.delivree.service;

import com.delivree.model.Product;
import com.delivree.model.Restaurant;
import com.delivree.model.Review;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class RestaurantService {
    private ArrayList<Restaurant> restaurants;

    public RestaurantService() {
        this.restaurants = new ArrayList<Restaurant>();
    }

    public void addRestaurant(Restaurant rest) {
        restaurants.add(rest);
    }

    public Optional<Restaurant> getRestaurantById(UUID restId) {
        return this.restaurants.stream()
                        .filter(r -> r.getRestaurantId() == restId)
                        .findFirst();
    }

    public Optional<ArrayList<Product>> getMenuByRestaurantId(UUID restId) {
        return this.restaurants.stream()
                .filter(r -> r.getRestaurantId() == restId)
                .findFirst()
                .map(r -> r.getProducts());
    }

    public void showRestaurants() {
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
                rest -> rest.getProducts().add(prod),
                () -> System.out.println("Restaurant not found!"));
    }

    public void addReviewToRestaurant(Review rev) {
        var restOpt = this.getRestaurantById(rev.getRestaurantId());
        restOpt.ifPresentOrElse(
                rest -> rest.getReviews().add(rev),
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
}
