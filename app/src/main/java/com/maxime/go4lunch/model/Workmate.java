package com.maxime.go4lunch.model;

public class Workmate {

    private String avatar;
    private String id;
    private String name;
    private String restaurant;
    private String restaurantID;
    private String restaurant_address;
    private String restaurant_date_choice;

    public Workmate () {}

    public Workmate(String id, String avatar, String name) {
        this.avatar = avatar;
        this.id = id;
        this.name = name;
        this.restaurant = "aucun";
        this.restaurantID = "unknow";
        this.restaurant_address = "unknow";
        this.restaurant_date_choice = "";
    }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name;}

    public String getRestaurantID() { return restaurantID; }

    public void setRestaurantID(String restaurantID) { this.restaurantID = restaurantID; }

    public String getRestaurant() { return restaurant; }

    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }

    public String getRestaurant_address() { return restaurant_address; }

    public void setRestaurant_address(String restaurant_address) { this.restaurant_address = restaurant_address; }

    public String getRestaurant_date_choice() { return restaurant_date_choice; }

    public void setRestaurant_date_choice(String restaurant_date_choice) { this.restaurant_date_choice = restaurant_date_choice; }

}
