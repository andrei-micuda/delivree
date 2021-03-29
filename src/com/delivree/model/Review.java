package com.delivree.model;

import java.util.UUID;

public class Review {
    private UUID userId;
    private UUID restaurantId;
    protected int rating;
    protected String message;

    public Review(UUID userId, UUID restaurantId, int rating, String message) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.rating + " stars\n"
                + this.message;
    }
}
