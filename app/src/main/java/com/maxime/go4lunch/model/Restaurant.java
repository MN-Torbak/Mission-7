package com.maxime.go4lunch.model;

import com.google.android.libraries.places.api.model.PlaceLikelihood;

public class Restaurant {
    private String urlAvatar;
    private String name;
    private String schedule;
    private String address;
    private String phoneNumber;
    private String webSite;

    public Restaurant(String avatar, String name, String schedule, String address, String phoneNumber, String webSite) {
        this.urlAvatar = avatar;
        this.name = name;
        this.schedule = schedule;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
    }

    public Restaurant(PlaceLikelihood placeLikelihood) {
        name = placeLikelihood.getPlace().getName();
        urlAvatar = placeLikelihood.getPlace().getPhotoMetadatas().toString();
        address = placeLikelihood.getPlace().getAddress();
        schedule = "5h";

    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

}
