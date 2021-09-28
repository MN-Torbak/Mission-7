package com.maxime.go4lunch.model;

import android.location.Location;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Restaurant implements Parcelable {

    private String id;
    private String urlAvatar;
    private String name;
    private String schedule;
    private String address;
    private String phoneNumber;
    private Uri webSite;
    private float star;
    private List<Workmate> workmatesEatingHere = new ArrayList<>();
    private List<Like> likes = new ArrayList<>();

    private LatLng latlng;

    public Restaurant(String id, String avatar, String name, String schedule, String address, String phoneNumber, Uri webSite, Integer star) {
        this.id = id;
        this.urlAvatar = avatar;
        this.name = name;
        this.schedule = schedule;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
        this.star = star;

    }

    public Restaurant(PlaceLikelihood placeLikelihood) {
        id = placeLikelihood.getPlace().getId();
        name = placeLikelihood.getPlace().getName();
        if (placeLikelihood.getPlace().getPhotoMetadatas() != null) {
            final List<PhotoMetadata> metadata = placeLikelihood.getPlace().getPhotoMetadatas();
            final PhotoMetadata photoMetadata = metadata.get(0);
            final String attributions = photoMetadata.zza();
            urlAvatar = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + attributions + "&sensor=true&key=" + "AIzaSyC5PnYLjeSjD1CHBrujXoKqUt0yozB86bk";

        } else {
            urlAvatar = "https://www.b2b-infos.com/wp-content/uploads/Fast-food-en-France.jpg";
        }
        address = placeLikelihood.getPlace().getAddress();
        phoneNumber = placeLikelihood.getPlace().getPhoneNumber();
        webSite = placeLikelihood.getPlace().getWebsiteUri();
        schedule = "?";
        latlng = placeLikelihood.getPlace().getLatLng();

    }

    public Restaurant(Place place) {
        id = place.getId();
        name = place.getName();
        if (place.getPhotoMetadatas() != null) {
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            final PhotoMetadata photoMetadata = metadata.get(0);
            final String attributions = photoMetadata.zza();
            urlAvatar = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + attributions + "&sensor=true&key=" + "AIzaSyC5PnYLjeSjD1CHBrujXoKqUt0yozB86bk";

        } else {
            urlAvatar = "https://www.b2b-infos.com/wp-content/uploads/Fast-food-en-France.jpg";
        }
        address = place.getAddress();
        phoneNumber = place.getPhoneNumber();
        webSite = place.getWebsiteUri();
        schedule = "?";
        latlng = place.getLatLng();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Uri getWebSite() {
        return webSite;
    }

    public void setWebSite(Uri webSite) {
        this.webSite = webSite;
    }

    public float getStar() {
        int stars = sum(getStarNumberForRestaurant());
        int likesSize = likes.size();
        if (likesSize == 0) {
            return 0;
        } else {
            return (float) stars/likesSize;
        }
    }

    public void setStar(Float star) {
        this.star = star;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public List<Workmate> getWorkmatesEatingHere() {
        return workmatesEatingHere;
    }

    public void setWorkmatesEatingHere(List<Workmate> workmatesEatingHere) {
        this.workmatesEatingHere = workmatesEatingHere;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
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
        parcel.writeParcelable(webSite, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
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
                    (Uri) in.readParcelable(getClass().getClassLoader()),
                    in.readInt());
        }

        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public List<Integer> getStarNumberForRestaurant() {
        List<Integer> starnumber = new ArrayList<>();
        for (Like like : likes) {
            starnumber.add(like.getStarNumber());
        }
        return starnumber;
    }

    public int sum(List<Integer> list) {
        int sum = 0;
        for (int i : list)
            sum = sum + i;
        return sum;
    }

    public static class RestaurantProxiComparator implements Comparator<Restaurant> {
        Location mLocation;
        public RestaurantProxiComparator(Location location) {
            mLocation = location;
        }
        @Override
        public int compare(Restaurant left, Restaurant right) {
            int distanceLeft = getRange(mLocation, left);
            int distanceRight = getRange(mLocation, right);
            return distanceLeft-distanceRight;
        }
    }

    public static class RestaurantStarsComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant left, Restaurant right) {
            Float starLeft = left.getStar();
            Float starRight = right.getStar();
            return starRight.compareTo(starLeft);
        }
    }

    public static class RestaurantNumberOfWorkmatesComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant left, Restaurant right) {
            return right.getWorkmatesEatingHere().size()-(left.getWorkmatesEatingHere().size());
        }
    }

    public static int getRange(Location myLocation, Restaurant restaurant) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), restaurant.getLatlng().latitude, restaurant.getLatlng().longitude, results);
        int distanceInMeters = (int) results[0];
        return distanceInMeters;
    }

}
