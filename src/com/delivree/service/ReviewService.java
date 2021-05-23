package com.delivree.service;

import com.delivree.model.Review;
import com.delivree.model.User;
import com.delivree.utils.CsvReadWrite;
import com.delivree.utils.DbLayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReviewService {
    private static ReviewService instance;
    private Connection _db = DbLayer.getInstance().getConnection();

    public static ReviewService getInstance() {
        if (instance == null) {
            instance = new ReviewService();
        }
        return instance;
    }

    private ArrayList<Review> reviews;

    private ReviewService() {
        this.reviews = new ArrayList<Review>();
    }

    public void addReview(Review rev) {
        this.reviews.add(rev);
    }

    public Optional<Review> getReviewByUserId(UUID userId) {
        return this.reviews.stream()
                .filter(r -> r.getUserId().equals(userId))
                .findFirst();
    }

    public void saveAll(String file_path) {
        if(this.reviews == null) return;
        CsvReadWrite.writeAll(this.reviews, file_path);
    }

    public void readAll(String file_path) {
        CsvReadWrite.readAll(file_path).ifPresent((csvs) -> {
            var lst = csvs.stream()
                    .map(csv -> Review.parse(csv))
                    .collect(Collectors.toList());
            this.reviews = new ArrayList(lst);
        });
    }

    public void insert(Review rev) {
        try{
            String sql = "INSERT INTO reviews\n" +
                    "VALUES (UUID_TO_BIN(?), UUID_TO_BIN(?), ?, ?);";
            var stmt = _db.prepareStatement(sql);
            stmt.setString(1, rev.getUserId().toString());
            stmt.setString(2, rev.getRestaurantId().toString());
            stmt.setInt(3, rev.getRating());
            stmt.setString(4, rev.getMessage());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
