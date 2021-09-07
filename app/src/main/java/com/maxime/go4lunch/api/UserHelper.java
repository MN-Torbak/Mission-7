package com.maxime.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Workmate;

import java.text.DateFormat;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_LIKE = "likes";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getLikesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKE);
    }

    // --- CREATE ---

    public static com.google.android.gms.tasks.Task<Void> createUser(String id, String avatar, String name) {
        Workmate userToCreate = new Workmate(id, avatar, name);
        return UserHelper.getUsersCollection().document(id).set(userToCreate);
    }

    public static com.google.android.gms.tasks.Task<Void> createLike(String id, String workmateId, String restaurantId) {
        Like likeToCreate = new Like(id, workmateId, restaurantId);
        return UserHelper.getLikesCollection().document(id).set(likeToCreate);
    }

    // --- GET ---

    public static com.google.android.gms.tasks.Task<DocumentSnapshot> getUser(String id) {
        return UserHelper.getUsersCollection().document(id).get();
    }

    public static com.google.android.gms.tasks.Task<DocumentSnapshot> getLike(String id) {
        return UserHelper.getLikesCollection().document(id).get();
    }


    // --- UPDATE ---

    public static com.google.android.gms.tasks.Task<Void> updateUserName(String id, String name) {
        return UserHelper.getUsersCollection().document(id).update("name", name);
    }

    public static com.google.android.gms.tasks.Task<Void> updateUserAvatar(String id, String avatar) {
        return UserHelper.getUsersCollection().document(id).update("avatar", avatar);
    }

    public static com.google.android.gms.tasks.Task<Void> updateUserRestaurant(String id, String restaurant) {
        return UserHelper.getUsersCollection().document(id).update("restaurant", restaurant);
    }

    public static com.google.android.gms.tasks.Task<Void> updateUserRestaurantAddress(String id, String restaurant_address) {
        return UserHelper.getUsersCollection().document(id).update("restaurant_address", restaurant_address);
    }

    public static com.google.android.gms.tasks.Task<Void> updateUserRestaurantID(String id, String restaurantID) {
        return UserHelper.getUsersCollection().document(id).update("restaurantID", restaurantID);
    }

    public static com.google.android.gms.tasks.Task<Void> updateUserRestaurantDateChoice(String id, String restaurant_date_choice) {
        return UserHelper.getUsersCollection().document(id).update("restaurant_date_choice", restaurant_date_choice);
    }

    public static com.google.android.gms.tasks.Task<Void> updateUserLikeRestaurant(String id, Integer starNumber) {
        return UserHelper.getLikesCollection().document(id).update("starNumber", starNumber);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String id) {
        return UserHelper.getUsersCollection().document(id).delete();
    }

}