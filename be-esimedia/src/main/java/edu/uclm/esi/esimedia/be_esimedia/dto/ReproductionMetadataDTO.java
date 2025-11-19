package edu.uclm.esi.esimedia.be_esimedia.dto;

public class ReproductionMetadataDTO {

    private int views;
    private double averageRating;
    private int userRating; // 0 si no ha valorado, 1-5 si ha valorado

    // Getters and Setters
    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }
}
