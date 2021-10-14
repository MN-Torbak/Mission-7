package com.maxime.go4lunch.api;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Workmate;

public class UserRepository {


    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_LIKE = "likes";

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public CollectionReference getLikesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKE);
    }

    public Task<Void> createUser(String id, String avatar, String name) {
        Workmate userToCreate = new Workmate(id, avatar, name);
        return this.getUsersCollection().document(id).set(userToCreate);
    }

    public Task<Void> createLike(String id, String workmateId, String restaurantId) {
        Like likeToCreate = new Like(id, workmateId, restaurantId);
        return this.getLikesCollection().document(id).set(likeToCreate);
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public com.google.android.gms.tasks.Task<DocumentSnapshot> getUser(String id) {
        return this.getUsersCollection().document(id).get();
    }

    public com.google.android.gms.tasks.Task<DocumentSnapshot> getLike(String id) {
        return this.getLikesCollection().document(id).get();
    }

    public Task<Void> updateUserName(String id, String name) {
        return this.getUsersCollection().document(id).update("name", name);
    }

    public Task<Void> updateUserAvatar(String id, String avatar) {
        return this.getUsersCollection().document(id).update("avatar", avatar);
    }

    public void updateUserRestaurant(String id, String restaurant) {
        this.getUsersCollection().document(id).update("restaurant", restaurant);
    }

    public void updateUserRestaurantAddress(String id, String restaurant_address) {
        this.getUsersCollection().document(id).update("restaurant_address", restaurant_address);
    }

    public void updateUserRestaurantID(String id, String restaurantID) {
        this.getUsersCollection().document(id).update("restaurantID", restaurantID);
    }

    public void updateUserRestaurantDateChoice(String id, String restaurant_date_choice) {
        this.getUsersCollection().document(id).update("restaurant_date_choice", restaurant_date_choice);
    }

    public void updateUserLikeRestaurant(String id, Integer starNumber) {
        this.getLikesCollection().document(id).update("starNumber", starNumber);
    }

    public void deleteUser(String id) {
        this.getUsersCollection().document(id).delete();
    }

}
