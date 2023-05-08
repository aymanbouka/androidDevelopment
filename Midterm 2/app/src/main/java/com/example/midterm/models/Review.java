package com.example.midterm.models;

import org.json.JSONObject;

public class Review {
    String review, rating, created_at;
/*
"review": "testing 3 for real",
            "rating": "3",
            "created_at": "2023-04-26 22:34:31"
 */

    @Override
    public String toString() {
        return "Review{" +
                "review='" + review + '\'' +
                ", rating='" + rating + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public Review() {
    }

    public Review(JSONObject json) {
  this.review = json.optString("review");
    this.rating = json.optString("rating");
    this.created_at = json.optString("created_at");


    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
