package com.delivree;

import com.delivree.model.*;
import com.delivree.service.*;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.GenericMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static final ProductService ps = ProductService.getInstance();
    public static final RestaurantService rstS = RestaurantService.getInstance();
    public static final DriverService ds = DriverService.getInstance();
    public static final OrderService os = OrderService.getInstance();
    public static final UserService us = UserService.getInstance();
    public static final ReviewService revS = ReviewService.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    private static final GenericMenu genericMenu = new GenericMenu();

    public static void showMenu() {
        // Add a menu item using a Lambda expression.
        genericMenu.addMenuItem("L", "Load data", () -> {
            us.readAll("users.csv");
            ds.readAll("drivers.csv");
            ps.readAll("products.csv");
            os.readAll("orders.csv");
            rstS.readAll("restaurants.csv");
            revS.readAll("reviews.csv");
        });

        genericMenu.addMenuItem("S", "Save changes", () -> {
            us.saveAll("users.csv");
            ds.saveAll("drivers.csv");
            ps.saveAll("products.csv");
            os.saveAll("orders.csv");
            rstS.saveAll("restaurants.csv");
            revS.saveAll("reviews.csv");
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
        });

        genericMenu.addMenuItem("listU", "List users", () -> {
            us.listUsers();
        });

        genericMenu.addMenuItem("addPrdToUsr", "Add a product to a user's cart", () -> {
            System.out.println("Which user does the cart belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
                int restChoice = scanner.nextInt();
                scanner.nextLine();
                while(restChoice <= 0 || restChoice > usrLst.size()){
                    restChoice = scanner.nextInt();
                    scanner.nextLine();
                }
                userId = usrLst.get(restChoice - 1).first;
            }

            System.out.println("Add products to the user's cart (-1 to finish adding)");
            var prodLst = ps.overviewProducts();
            if(prodLst == null) return;

            while(true) {
                UUID prodId = null;
                for (int i = 0; i < prodLst.size(); i++) {
                    System.out.println((i+1) + ") " + prodLst.get(i).second);
                    int restChoice = scanner.nextInt();
                    scanner.nextLine();
                    if(restChoice == -1) return;
                    while(restChoice <= 0 || restChoice > prodLst.size()){
                        restChoice = scanner.nextInt();
                        scanner.nextLine();
                    }
                    prodId = prodLst.get(restChoice - 1).first;
                    us.addProductToUserCart(prodId, userId);
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
                int restChoice = scanner.nextInt();
                scanner.nextLine();
                while(restChoice <= 0 || restChoice > usrLst.size()){
                    restChoice = scanner.nextInt();
                    scanner.nextLine();
                }
                userId = usrLst.get(restChoice - 1).first;
            }
            us.showCart(userId);
        });

        genericMenu.addMenuItem("createOrd", "Create an order", () -> {
            System.out.println("Which user does the order belong to?");
            var usrLst = us.overviewUsers();
            if(usrLst == null) return;

            UUID userId = null;
            for (int i = 0; i < usrLst.size(); i++) {
                System.out.println((i+1) + ") " + usrLst.get(i).second);
                int restChoice = scanner.nextInt();
                scanner.nextLine();
                while(restChoice <= 0 || restChoice > usrLst.size()){
                    restChoice = scanner.nextInt();
                    scanner.nextLine();
                }
                userId = usrLst.get(restChoice - 1).first;
            }
            us.createOrder(userId);
        });

        genericMenu.addMenuItem("addPrd", "Add a new product", () -> {
            System.out.println("Which restaurant does the product belong to?");
            var restLst = rstS.restaurantsOverview();
            UUID restId = null;
            for (int i = 0; i < restLst.size(); i++) {
                System.out.println((i+1) + ") " + restLst.get(i));
                int restChoice = scanner.nextInt();
                scanner.nextLine();
                while(restChoice <= 0 || restChoice > restLst.size()){
                    restChoice = scanner.nextInt();
                    scanner.nextLine();
                }
                restId = rstS.getRestaurantIdFromName(restLst.get(restChoice - 1)).get();
            }
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
        });

        genericMenu.addMenuItem("listPrd", "List products", () -> {
            ps.listProducts();
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
        });

        genericMenu.addMenuItem("listRst", "List restaurants", () -> {
            rstS.listRestaurants();
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
        });

        genericMenu.addMenuItem("listDrv", "List drivers", () -> {
            ds.listDrivers();
        });
        genericMenu.initMenu();
    }


    public static void main(String[] args) {
        showMenu();
    }
}
