package com.delivree.service;

import com.delivree.model.Order;
import com.delivree.model.Product;
import com.delivree.model.Restaurant;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductService {
    private static ProductService instance;

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    private ArrayList<Product> products;

    private ProductService() {
        products = new ArrayList<Product>();
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void listProducts() {
        for(var p : this.products) {
            System.out.println(p.toString());
        }
    }

    public ArrayList<Pair<UUID, String>> overviewProducts() {
        var res = new ArrayList<Pair<UUID, String>>();
        for (var p : products) {
            res.add(new Pair(p.getProductId(), p.getName()));
        }
        return res;
    }

    public Optional<Product> getProductById(UUID productId) {
        return this.products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst();
    }

    public void saveAll(String file_path) {
        if(this.products == null) return;
        CsvReadWrite.writeAll(this.products, file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> Product.parse(csv))
                    .collect(Collectors.toList());
            this.products = new ArrayList(lst);
        });
    }
}
