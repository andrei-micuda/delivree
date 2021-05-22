package com.delivree;

import com.delivree.model.*;
import com.delivree.service.*;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.DbLayer;
import com.delivree.utils.GenericMenu;
import com.delivree.utils.Logger;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static final AddressService as = AddressService.getInstance();
    public static final ProductService ps = ProductService.getInstance();
    public static final RestaurantService rstS = RestaurantService.getInstance();
    public static final DriverService ds = DriverService.getInstance();
    public static final OrderService os = OrderService.getInstance();
    public static final UserService us = UserService.getInstance();
    public static final ReviewService revS = ReviewService.getInstance();

    private static final Scanner scanner = new Scanner(System.in);

    private static final Logger logger = new Logger("logs.csv");

    private static final GenericMenu genericMenu = new GenericMenu();

    public static void showCSVMenu() {
        genericMenu.addMenuItem("L", "Load data", () -> {
            us.readAll("users.csv");
            ds.readAll("drivers.csv");
            ps.readAll("products.csv");
            os.readAll("orders.csv");
            rstS.readAll("restaurants.csv");
            revS.readAll("reviews.csv");

            logger.write("Loaded data");
        });

        genericMenu.addMenuItem("S", "Save changes", () -> {
            us.saveAll("users.csv");
            ds.saveAll("drivers.csv");
            ps.saveAll("products.csv");
            os.saveAll("orders.csv");
            rstS.saveAll("restaurants.csv");
            revS.saveAll("reviews.csv");

            logger.write("Saved data");
        });

        genericMenu.addMenuItem("addU", "Add a new user", () -> {
            System.out.print("First name: ");
            var firstName = scanner.nextLine();
            System.out.print("Last name: ");
            var lastName = scanner.nextLine();
            System.out.print("Age: ");
            var age = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Street: ");
            var street = scanner.nextLine();
            System.out.print("Number: ");
            var number = scanner.nextInt();
            scanner.nextLine();
            System.out.print("City: ");
            var city = scanner.nextLine();
            us.addUser(new User(firstName, lastName, age, new Address(street, number, city)));

            logger.write("Added user");
        });

        genericMenu.addMenuItem("listU", "List users", () -> {
            us.listUsers();
            logger.write("Listed users");
        });

        genericMenu.addMenuItem("addPrdToUsr", "Add a product to a user's cart", () -> {
            System.out.println("Which user does the cart belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Add products to the user's cart (-1 to finish adding)");
            var prodLst = ps.overviewProducts();
            if(prodLst == null) return;

            while(true) {
                UUID prodId = null;
                for (int i = 0; i < prodLst.size(); i++) {
                    System.out.println((i+1) + ") " + prodLst.get(i).second);
                }
                int restChoice = scanner.nextInt();
                scanner.nextLine();
                if(restChoice == -1){
                    logger.write("Added products to user");
                    return;
                }
                while(restChoice <= 0 || restChoice > prodLst.size()){
                    restChoice = scanner.nextInt();
                    scanner.nextLine();
                }
                prodId = prodLst.get(restChoice - 1).first;
                try{
                    us.addProductToUserCart(prodId, userId);
                }
                catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        genericMenu.addMenuItem("showUsrCart", "Show a user's cart", () -> {
            System.out.println("Which user does the cart belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > usrLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(restChoice - 1).first;

            try{
                us.showCart(userId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Retrieved user cart");
        });

        genericMenu.addMenuItem("listOrd", "List orders", () -> {
            os.showOrders();
            logger.write("Retrieved orders");
        });

        genericMenu.addMenuItem("createOrd", "Create an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > usrLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(restChoice - 1).first;

            try{
                us.createOrder(userId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Created order");
        });

        genericMenu.addMenuItem("cancelOrd", "Cancel an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > usrLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(restChoice - 1).first;

            System.out.println("Which order do you want to cancel?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }

            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;
            os.cancelOrder(ordId);

            logger.write("Cancel order");
        });

        genericMenu.addMenuItem("getOrdStatus", "Show the status of a certain order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which order do you want to get the status of?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }

            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;
            os.getOrderStatus(ordId);

            logger.write("Retrieved order status");
        });

        genericMenu.addMenuItem("completeOrder", "Complete a certain order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which order do you want to complete?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }
            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;

            try {
                os.completeOrder(ordId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Completed order");
        });

        genericMenu.addMenuItem("addPrd", "Add a new product", () -> {
            System.out.println("Which restaurant does the product belong to?");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i));
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            System.out.print("Product name: ");
            var productName = scanner.nextLine();
            System.out.print("Price: ");
            var price = scanner.nextFloat();
            scanner.nextLine();
            System.out.print("Ingredients (separated by commas): ");
            var ingredients = scanner.nextLine().split(",");
            var lst = Stream.of(ingredients)
                    .map(i -> i.strip())
                    .toArray(String[]::new);

            ps.addProduct(new Product(restId, productName, price, lst));

            logger.write("Added product");
        });

        genericMenu.addMenuItem("listPrd", "List products", () -> {
            ps.listProducts();

            logger.write("Retrieved products");
        });

        genericMenu.addMenuItem("addRst", "Add a new restaurant", () -> {
            System.out.print("Name: ");
            var name = scanner.nextLine();
            System.out.print("Description: ");
            var desc = scanner.nextLine();
            System.out.print("Street: ");
            var street = scanner.nextLine();
            System.out.print("Number: ");
            var number = scanner.nextInt();
            scanner.nextLine();
            System.out.print("City: ");
            var city = scanner.nextLine();

            rstS.addRestaurant(new Restaurant(name, desc, new Address(street, number, city)));

            logger.write("Added restaurant");
        });

        genericMenu.addMenuItem("listRst", "List restaurants", () -> {
            rstS.listRestaurants();

            logger.write("Retrieved restaurants");
        });

        genericMenu.addMenuItem("showRstMenu", "Show the menu of a restaurant", () -> {
            System.out.println("Restaurant:");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i));
            }

            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            try {
                rstS.showMenu(restId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Retrieved restaurant menu");
        });

        genericMenu.addMenuItem("addDrv", "Add a new driver", () -> {
            System.out.print("First name: ");
            var firstName = scanner.nextLine();
            System.out.print("Last name: ");
            var lastName = scanner.nextLine();
            System.out.print("Age: ");
            var age = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Vehicle " + Arrays.toString(Vehicle.values()) + ": ");
            var vehicle = scanner.nextLine();
            Vehicle enumVehicle = null;
            while(enumVehicle == null) {
                try{
                    enumVehicle = Vehicle.valueOf(vehicle);
                    break;
                }
                catch(IllegalArgumentException ex) {
                    System.out.print("Vehicle " + Arrays.toString(Vehicle.values()) + ": ");
                    vehicle = scanner.nextLine();
                }
            }
            ds.addDriver(new Driver(firstName, lastName, age, enumVehicle));

            logger.write("Added driver");
        });

        genericMenu.addMenuItem("listDrv", "List drivers", () -> {
            ds.listDrivers();

            logger.write("Retrieved drivers");
        });

        genericMenu.addMenuItem("assignDrvToOrd", "Assign a driver to an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which order do you want to assign a driver to?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }
            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;

            try {
                os.assignDriverToOrder(ordId, ds.getFirstAvailableDriver());
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Assigned driver to order");
        });

        genericMenu.addMenuItem("addRev", "Add a review to a restaurant", () -> {
            System.out.println("Which user does the review belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which restaurant does the review belong to?");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i));
            }

            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            System.out.print("Rating(1-5): ");
            int rating = scanner.nextInt();
            scanner.nextLine();
            while(rating < 1 || rating > 5){
                rating = scanner.nextInt();
                scanner.nextLine();
            }

            System.out.println("Message (no commas, one line):");
            String message = scanner.nextLine();
            revS.addReview(new Review(userId, restId, rating, message));

            logger.write("Added review to restaurant");
        });

        genericMenu.addMenuItem("listRev", "Show the reviews of a restaurant", () -> {
            System.out.println("Restaurant:");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i));
            }

            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            try {
                rstS.showRestaurantReviews(restId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Retrieved restaurant reviews");
        });

        genericMenu.initMenu();
    }

    public static void showMenu() {
        // DONE
        genericMenu.addMenuItem("addU", "Add a new user", () -> {
            System.out.print("First name: ");
            var firstName = scanner.nextLine();
            System.out.print("Last name: ");
            var lastName = scanner.nextLine();
            System.out.print("Age: ");
            var age = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Street: ");
            var street = scanner.nextLine();
            System.out.print("Number: ");
            var number = scanner.nextInt();
            scanner.nextLine();
            System.out.print("City: ");
            var city = scanner.nextLine();
            us.insert(new User(firstName, lastName, age, new Address(street, number, city)));

            logger.write("Added user");
        });

        // DONE
        genericMenu.addMenuItem("listU", "List users", () -> {
            us.listUsers();
            logger.write("Listed users");
        });

        // DONE
        genericMenu.addMenuItem("addPrdToUsr", "Add a product to a user's cart", () -> {
            System.out.println("Which user does the cart belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Add products to the user's cart (-1 to finish adding)");
            var prodLst = ps.overviewProducts();
            if(prodLst == null) return;

            while(true) {
                UUID prodId = null;
                for (int i = 0; i < prodLst.size(); i++) {
                    System.out.println((i+1) + ") " + prodLst.get(i).second);
                }
                int restChoice = scanner.nextInt();
                scanner.nextLine();
                if(restChoice == -1){
                    logger.write("Added products to user");
                    return;
                }
                while(restChoice <= 0 || restChoice > prodLst.size()){
                    restChoice = scanner.nextInt();
                    scanner.nextLine();
                }
                prodId = prodLst.get(restChoice - 1).first;
                try{
                    us.addProductToUserCart(prodId, userId);
                }
                catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        // DONE
        genericMenu.addMenuItem("showUsrCart", "Show a user's cart", () -> {
            System.out.println("Which user does the cart belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > usrLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(restChoice - 1).first;

            try{
                us.showCart(userId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Retrieved user cart");
        });

        // DONE
        genericMenu.addMenuItem("listOrd", "List orders", () -> {
            os.showOrders();
            logger.write("Retrieved orders");
        });

        // DONE
        genericMenu.addMenuItem("createOrd", "Create an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > usrLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(restChoice - 1).first;

            try{
                us.createOrder(userId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Created order");
        });

        // DONE
        genericMenu.addMenuItem("cancelOrd", "Cancel an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > usrLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(restChoice - 1).first;

            System.out.println("Which order do you want to cancel?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }

            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;
            os.cancelOrder(ordId);

            logger.write("Cancel order");
        });

        // DONE
        genericMenu.addMenuItem("getOrdStatus", "Show the status of a certain order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which order do you want to get the status of?");
            var ordLst = os.overviewOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }

            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;
            os.getOrderStatus(ordId);

            logger.write("Retrieved order status");
        });

        // DONE -> mai e partea cu driver ul
        genericMenu.addMenuItem("completeOrder", "Complete a certain order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which order do you want to complete?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }
            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;

            try {
                os.completeOrder(ordId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Completed order");
        });

        // DONE
        genericMenu.addMenuItem("addPrd", "Add a new product", () -> {
            System.out.println("Which restaurant does the product belong to?");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i).first);
            }
            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            System.out.print("Product name: ");
            var productName = scanner.nextLine();
            System.out.print("Price: ");
            var price = scanner.nextFloat();
            scanner.nextLine();
            System.out.print("Ingredients (separated by commas): ");
            var ingredients = scanner.nextLine().split(",");
            var lst = Stream.of(ingredients)
                    .map(i -> i.strip())
                    .toArray(String[]::new);

            ps.insert(new Product(restId, productName, price, lst));

            logger.write("Added product");
        });

        // DONE
        genericMenu.addMenuItem("listPrd", "List products", () -> {
            ps.listProducts();

            logger.write("Retrieved products");
        });

        // DONE
        genericMenu.addMenuItem("addRst", "Add a new restaurant", () -> {
            System.out.print("Name: ");
            var name = scanner.nextLine();
            System.out.print("Description: ");
            var desc = scanner.nextLine();
            System.out.print("Street: ");
            var street = scanner.nextLine();
            System.out.print("Number: ");
            var number = scanner.nextInt();
            scanner.nextLine();
            System.out.print("City: ");
            var city = scanner.nextLine();

            rstS.insert(new Restaurant(name, desc, new Address(street, number, city)));

            logger.write("Added restaurant");
        });

        // DONE
        genericMenu.addMenuItem("listRst", "List restaurants", () -> {
            rstS.listRestaurants();

            logger.write("Retrieved restaurants");
        });

        // DONE
        genericMenu.addMenuItem("showRstMenu", "Show the menu of a restaurant", () -> {
            System.out.println("Restaurant:");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i).first);
            }

            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            try {
                rstS.showMenu(restId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Retrieved restaurant menu");
        });

        // DONE
        genericMenu.addMenuItem("addDrv", "Add a new driver", () -> {
            System.out.print("First name: ");
            var firstName = scanner.nextLine();
            System.out.print("Last name: ");
            var lastName = scanner.nextLine();
            System.out.print("Age: ");
            var age = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Vehicle " + Arrays.toString(Vehicle.values()) + ": ");
            var vehicle = scanner.nextLine();
            Vehicle enumVehicle = null;
            while(enumVehicle == null) {
                try{
                    enumVehicle = Vehicle.valueOf(vehicle);
                    break;
                }
                catch(IllegalArgumentException ex) {
                    System.out.print("Vehicle " + Arrays.toString(Vehicle.values()) + ": ");
                    vehicle = scanner.nextLine();
                }
            }
            ds.insert(new Driver(firstName, lastName, age, enumVehicle));

            logger.write("Added driver");
        });

        genericMenu.addMenuItem("listDrv", "List drivers", () -> {
            ds.listDrivers();

            logger.write("Retrieved drivers");
        });

        genericMenu.addMenuItem("assignDrvToOrd", "Assign a driver to an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which order do you want to assign a driver to?");
            var ordLst = os.overviewUncompletedOrdersByUserId(userId);
            if(ordLst == null) return;

            UUID ordId = null;
            for (int i = 0; i < ordLst.size(); i++) {
                System.out.println((i+1) + ") " + ordLst.get(i).second);
            }
            int ordChoice = scanner.nextInt();
            scanner.nextLine();
            while(ordChoice <= 0 || ordChoice > ordLst.size()){
                ordChoice = scanner.nextInt();
                scanner.nextLine();
            }
            ordId = ordLst.get(ordChoice - 1).first;

            try {
                os.assignDriverToOrder(ordId, ds.getFirstAvailableDriver());
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Assigned driver to order");
        });

        genericMenu.addMenuItem("addRev", "Add a review to a restaurant", () -> {
            System.out.println("Which user does the review belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
            }
            int usrChoice = scanner.nextInt();
            scanner.nextLine();
            while(usrChoice <= 0 || usrChoice > usrLst.size()){
                usrChoice = scanner.nextInt();
                scanner.nextLine();
            }
            userId = usrLst.get(usrChoice - 1).first;

            System.out.println("Which restaurant does the review belong to?");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i).first);
            }

            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            System.out.print("Rating(1-5): ");
            int rating = scanner.nextInt();
            scanner.nextLine();
            while(rating < 1 || rating > 5){
                rating = scanner.nextInt();
                scanner.nextLine();
            }

            System.out.println("Message (no commas, one line):");
            String message = scanner.nextLine();
            revS.addReview(new Review(userId, restId, rating, message));

            logger.write("Added review to restaurant");
        });

        genericMenu.addMenuItem("listRev", "Show the reviews of a restaurant", () -> {
            System.out.println("Restaurant:");
            var restLst = rstS.overviewRestaurants();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i).first);
            }

            int restChoice = scanner.nextInt();
            scanner.nextLine();
            while(restChoice <= 0 || restChoice > restLst.size()){
                restChoice = scanner.nextInt();
                scanner.nextLine();
            }
            restId = restLst.get(restChoice - 1).second;

            try {
                rstS.showRestaurantReviews(restId);
            }
            catch(Exception ex) {
                System.out.println(ex);
            }

            logger.write("Retrieved restaurant reviews");
        });

        genericMenu.initMenu();
    }

    public static void main(String[] args) throws SQLException {
//        showCSVMenu();
        showMenu();
    }
}
