package com.delivree.model;

import java.util.ArrayList;
import java.util.UUID;

public class Product {
    static final int MAX_NUM_INGREDIENTS = 20;
    private UUID productId = UUID.randomUUID();
    protected String name;
    protected float price;
    protected String[] ingredients;

    public Product(String name, float price, String[] ingredients) {
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
}
