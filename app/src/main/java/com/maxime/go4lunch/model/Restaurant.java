package com.maxime.go4lunch.model;

public class Restaurant {
    private String UrlAvatar;
    private String Name;
    private String Info;
    private String Address;
    private String PhoneNumber;
    private String WebSite;

    public Restaurant(String avatar, String name, String info, String address, String phoneNumber, String webSite) {
        this.UrlAvatar = avatar;
        this.Name = name;
        this.Info = info;
        this.Address = address;
        this.PhoneNumber = phoneNumber;
        this.WebSite = webSite;
    }

    public String getUrlAvatar() { return UrlAvatar; }

    public void setUrlAvatar(String urlAvatar) { UrlAvatar = urlAvatar; }

    public String getName() { return Name; }

    public void setName(String name) { Name = name; }

    public String getInfo() { return Info; }

    public void setInfo(String info) { Info = info; }

    public String getAddress() { return Address; }

    public void setAddress(String address) { Address = address; }

    public String getPhoneNumber() { return PhoneNumber; }

    public void setPhoneNumber(String phoneNumber) { PhoneNumber = phoneNumber; }

    public String getWebSite() { return WebSite; }

    public void setWebSite(String webSite) { WebSite = webSite; }

}
