package com.delivree.model;

import com.delivree.utils.ICsvConvertible;

import java.util.ArrayList;
import java.util.UUID;

public class Product implements ICsvConvertible<Product> {
    static final int MAX_NUM_INGREDIENTS = 20;
    private UUID productId;
    protected String name;
    protected float price;
    protected String[] ingredients;
    protected UUID restaurantId;

    public UUID getProductId() {
        return productId;
    }

    public Product(UUID restaurantId, UUID productId, String name, float price, String[] ingredients) {
        this.restaurantId = restaurantId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        if(ingredients.length > MAX_NUM_INGREDIENTS) {
            System.out.println("A product can have at most 20 ingredients.");
        }
        else {
            this.ingredients = new String[ingredients.length];
            for (int i = 0; i < ingredients.length; i++) {
                this.ingredients[i] = ingredients[i];
            }
        }

    }

    public Product(UUID restaurantId, String name, float price, String[] ingredients) {
        this.restaurantId = restaurantId;
        this.productId = UUID.randomUUID();
        this.name = name;
        this.price = price;
        if(ingredients.length > MAX_NUM_INGREDIENTS) {
            System.out.println("A product can have at most 20 ingredients.");
        }
        else {
            this.ingredients = new String[ingredients.length];
            for (int i = 0; i < ingredients.length; i++) {
                this.ingredients[i] = ingredients[i];
            }
        }

    }

    public Product(Product other) {
        this.restaurantId = other.restaurantId;
        this.productId = other.productId;
        this.name = other.name;
        this.price = other.price;
        this.ingredients = other.ingredients.clone();
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String printIngredients() {
        String res = "";
        for (int i = 0; i < ingredients.length; i++) {
            res += ingredients[i] + ", ";
        }
        return res;
    }

    public String summary() {
        return this.name +
                " Price: " + this.price;
    }

    @Override
    public String toString() {
        return this.name +
                "\nPrice: " + this.price +
                "\nIngredients: " + this.printIngredients();
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients.clone();
    }

    @Override
    public String[] stringify() {
        // restaurantId, productId, productName, productPrice, numOfIngredients, ingredient1, ..., ingredientn
        var s = new ArrayList<String>();
        s.add(this.restaurantId.toString());
        s.add(this.productId.toString());
        s.add(this.name);
        s.add(Float.toString(this.price));
        s.add(Integer.toString(this.ingredients.length));
        for(var i : this.ingredients){
            s.add(i);
        }
        return (String[])s.toArray(new String[0]);
    }

    public static Product parse(String csv) {
        var parts = csv.split(",");
        UUID restaurantId = UUID.fromString(parts[0]);
        UUID productId = UUID.fromString(parts[1]);
        String productName = parts[2];
        float price = Float.parseFloat(parts[3]);
        var ingredientsLst = new ArrayList<String>();
        for (int i = 0; i < Integer.parseInt(parts[4]); i++) {
            ingredientsLst.add(parts[5 + i]);
        }
        var ingredients = (String[])ingredientsLst.toArray(new String[0]);
        return new Product(restaurantId, productId, productName, price, ingredients);
    }
}
