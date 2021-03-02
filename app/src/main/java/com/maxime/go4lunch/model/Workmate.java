package com.maxime.go4lunch.model;

public class Workmate {

    private String avatar;
    private String id;
    private String name;
    private String restaurant;

    public Workmate () {}

    public Workmate(String avatar, String id, String name) {
        this.avatar = avatar;
        this.id = id;
        this.name = name;
        this.restaurant = "n'a pas choisi";
    }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name;}

    public String getRestaurant() { return restaurant; }

    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }

}
