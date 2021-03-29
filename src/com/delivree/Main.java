package com.delivree;

import com.delivree.model.*;
import com.delivree.service.DriverService;
import com.delivree.service.OrderService;
import com.delivree.service.RestaurantService;
import com.delivree.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final RestaurantService rs = new RestaurantService();
    public static final DriverService ds = new DriverService();
    public static final OrderService os = new OrderService(ds);
    public static final UserService us = new UserService(os);


    public static void main(String[] args) {
        Product p1 = new Product(
                "Burger",
                12.00f,
                new ArrayList<String> (List.of(
                        "Carne de vita",
                        "Ceapa",
                        "Branza"
                )));
        Restaurant r1 = new Restaurant(
                "Victoriei 18",
                "Victoriei 18 s-a transformat intr-un loc dedicat street food-ului din intreaga lume! Ne place sa calatorim prin lume, sa luam strazile la picior si sa mancam cu pofta din mancarea autentic locala care se serveste pe strada, fierbinte si gustoasa.",
                new Address("Calea Victoriei", 18, "Bucharest")
        );

        rs.addRestaurant(r1);
        rs.addProductToRestaurant(p1, r1.getRestaurantId());

        rs.showRestaurants();
        rs.showMenu(r1.getRestaurantId());

        User u1 = new User("Andrei", "Micuda", 21, new Address("Calea Vitan", 219, "Bucharest"));
        us.addUser(u1);
        us.addProductToUserCart(p1, u1.getUserId());
        us.showCart(u1.getUserId());

        us.createOrder(u1.getUserId());
        os.showOrders();

        var d1 = new Driver("Ion", "Popescu", 30, Vehicle.Bicycle);
        ds.addDriver(d1);

        var orders = os.getUncompletedOrdersByUserId(u1.getUserId());
        os.assignDriverToOrder(orders.get(0).getOrderId(), ds.getFirstAvailableDriver());
        os.completeOrder(orders.get(0).getOrderId());
        ds.showDrivers();

        var rev1 = new Review(u1.getUserId(), r1.getRestaurantId(), 5, "Very tasty food!");
        rs.addReviewToRestaurant(rev1);
        rs.showRestaurantReviews(r1.getRestaurantId());
    }
}
