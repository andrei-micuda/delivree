package com.delivree.model;

import com.delivree.service.AddressService;
import com.delivree.service.ReviewService;
import com.delivree.utils.ICsvConvertible;
import com.delivree.utils.Pair;

import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class Restaurant implements ICsvConvertible<Restaurant> {
    private UUID restaurantId;
    protected String name;
    protected ArrayList<UUID> products;
    protected ArrayList<UUID> reviews;
    protected int locationId;
    protected Address location;
    protected String description;
    private ReviewService rs;
    private AddressService as;

    public Restaurant(UUID restaurantId, String name, String description, Address location, ArrayList<UUID> products, ArrayList<UUID> reviews) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.products = products;
        this.reviews = reviews;
        this.location = location;
        this.rs = ReviewService.getInstance();
    }

    public Restaurant(UUID restaurantId, String name, String description, int locationId) {
        this.as = AddressService.getInstance();
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.products = new ArrayList<UUID>();
        this.reviews = new ArrayList<UUID>();
        this.locationId = locationId;
        this.location = as.getById(locationId);
        this.location = location;
        this.rs = ReviewService.getInstance();
    }

    public Restaurant(String name, String description, Address location) {
        this.restaurantId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.products = new ArrayList<UUID>();
        this.reviews = new ArrayList<UUID>();
        this.location = location;
        this.rs = ReviewService.getInstance();
    }

    public void addReview(UUID reviewId) {
        this.reviews.add(reviewId);
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public ArrayList<UUID> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<UUID> products) {
        this.products = products;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Restaurant " + this.name +
                "\nDescription: " + this.description +
                "\nAddress: " + this.location.toString();
    }

    public ArrayList<Review> getReviews() {
        var lst =  reviews.stream()
                .filter(rev -> rs.getReviewByUserId(rev).isPresent())
                .map(rev -> rs.getReviewByUserId(rev).get())
                .collect(Collectors.toList());
        return new ArrayList<Review>(lst);
    }

    @Override
    public String[] stringify() {
        // restaurantId, name, description, street, number, city, numberOfProductsInMenu, productId1, ..., productIdn, numberOfReviews, reviewUserId1, ..., reviewUserIdn
        ArrayList s = new ArrayList<String>();
        s.add(this.restaurantId.toString());
        s.add(this.name);
        s.add(this.description);
        s.add(this.location.getStreet());
        s.add(Integer.toString(this.location.getNumber()));
        s.add(this.location.getCity());
        s.add(Integer.toString(this.products.size()));
        for(var p : this.products){
            s.add(p.toString());
        }
        s.add(Integer.toString(this.reviews.size()));
        for(var r : this.reviews){
            s.add(r.toString());
        }
        return (String[])s.toArray(new String[0]);
    }

    public static Restaurant parse(String csv) {
        var parts = csv.split(",");
        UUID restaurantId = UUID.fromString(parts[0]);
        String name = parts[1];
        String description = parts[2];
        Address location = new Address(
                parts[3], // street
                Integer.parseInt(parts[4]), // number
                parts[5]); // city
        var products = new ArrayList<UUID>();
        for (int i = 0; i < Integer.parseInt(parts[6]); i++) {
            products.add(UUID.fromString(parts[7 + i]));
        }
        var reviews = new ArrayList<UUID>();
        for (int i = 0; i < Integer.parseInt(parts[7 + Integer.parseInt(parts[6])]); i++) {
            reviews.add(UUID.fromString(parts[8 + Integer.parseInt(parts[6]) + i]));
        }
        return new Restaurant(restaurantId, name, description, location, products, reviews);
    }
}
