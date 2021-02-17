package com.maxime.go4launch.model;

public class Workmate {

    private String id;
    private String avatar;
    private String name;
    private String restaurant;

    public Workmate(String id, String avatar, String name) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.restaurant = "n'a pas choisi";
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { }

    public String getName() { return name; }

    public void setName(String name) { }

    public String getRestaurant() { return restaurant; }

    public void setRestaurant(String restaurant) { }

}
