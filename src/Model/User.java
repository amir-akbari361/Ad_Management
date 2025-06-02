package Model;

import java.io.Serializable;

public class User implements Serializable {

    private String username;
    private String email;
    private String password;
    private double averageRating;
    private int ratingsCount;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.averageRating = 0.0;
        this.ratingsCount = 0;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    public int getRatingsCount() {
        return ratingsCount;
    }
    public double getAverageRating() {
        return averageRating;
    }
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }
    /*public void rateUser(int rating) {
        ratings.add(rating);
    }
    public double getAverageRating() {
        if(ratings.isEmpty())
        {
            return 0.0;
        }
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }*/
    @Override
    public String toString() {
        return username + "," + email + "," + password + " (Rating: " + averageRating + ")";
    }}


