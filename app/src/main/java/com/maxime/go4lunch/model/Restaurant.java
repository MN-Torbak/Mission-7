package com.maxime.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.PlaceLikelihood;


import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Parcelable {

    private String id;
    private String urlAvatar;
    private String name;
    private String schedule;
    private String address;
    private String phoneNumber;
    private String webSite;
    private List<Workmate> workmatesBeEating = new ArrayList<>();

    private LatLng latlng;

    public Restaurant(String id, String avatar, String name, String schedule, String address, String phoneNumber, String webSite) {
        this.id = id;
        this.urlAvatar = avatar;
        this.name = name;
        this.schedule = schedule;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;

    }

    public Restaurant(PlaceLikelihood placeLikelihood) {
        id = placeLikelihood.getPlace().getId();
        name = placeLikelihood.getPlace().getName();
        if (placeLikelihood.getPlace().getPhotoMetadatas()!= null) {
            final List<PhotoMetadata> metadata = placeLikelihood.getPlace().getPhotoMetadatas();
            final PhotoMetadata photoMetadata = metadata.get(0);
            final String attributions = photoMetadata.zza();
            urlAvatar = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + attributions + "&sensor=true&key=" + "AIzaSyC5PnYLjeSjD1CHBrujXoKqUt0yozB86bk";

        } else {
            urlAvatar = "https://www.b2b-infos.com/wp-content/uploads/Fast-food-en-France.jpg";
        }
        address = placeLikelihood.getPlace().getAddress();
        schedule = "5g";
        latlng = placeLikelihood.getPlace().getLatLng();

    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public LatLng getLatlng() { return latlng; }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public List<Workmate> getWorkmatesBeEating() {
        return workmatesBeEating;
    }

    public void setWorkmatesBeEating(List<Workmate> workmatesBeEating) {
        this.workmatesBeEating = workmatesBeEating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(urlAvatar);
        parcel.writeString(name);
        parcel.writeString(schedule);
        parcel.writeString(address);
        parcel.writeString(phoneNumber);
        parcel.writeString(webSite);
    }

    public static final Parcelable.Creator<Restaurant> CREATOR
            = new Parcelable.Creator<Restaurant>() {
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString(),
                    in.readString());
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };







}
