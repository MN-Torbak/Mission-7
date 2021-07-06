package com.maxime.go4lunch.model;

public class Like {

    private String id;
    private String workmateId;
    private String restaurantId;
    private Integer starNumber;

    public Like () {}

    public Like (String id, String workmateId, String restaurantId) {
        this.id = id;
        this.workmateId = workmateId;
        this.restaurantId = restaurantId;
        this.starNumber = 0;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getWorkmateId() { return workmateId; }

    public void setWorkmateId(String workmateId) { this.workmateId = workmateId; }

    public String getRestaurantId() { return restaurantId; }

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public Integer getStarNumber() { return starNumber; }

    public void setStarNumber(Integer starNumber) { this.starNumber = starNumber; }

}
