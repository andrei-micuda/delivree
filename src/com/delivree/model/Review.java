package com.delivree.model;

import com.delivree.utils.ICsvConvertible;

import java.util.ArrayList;
import java.util.UUID;

public class Review implements ICsvConvertible<Review> {
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

    @Override
    public String[] stringify() {
        // userId, restaurantId, rating, message
        ArrayList s = new ArrayList<String>();
        s.add(this.userId.toString());
        s.add(this.restaurantId.toString());
        s.add(Integer.toString(this.rating));
        s.add(this.message);
        return (String[])s.toArray(new String[0]);
    }

    public static Review parse(String csv) {
        var parts = csv.split(",");
        UUID userId = UUID.fromString(parts[0]);
        UUID restaurantId = UUID.fromString(parts[1]);
        int rating = Integer.parseInt(parts[2]);
        String message = parts[3];
        return new Review(userId, restaurantId, rating, message);
    }
}
