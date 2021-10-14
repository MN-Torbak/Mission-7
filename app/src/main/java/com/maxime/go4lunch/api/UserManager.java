package com.maxime.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserManager {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_LIKE = "likes";

    UserRepository mUserRepository;
    public UserManager() {
        mUserRepository = new UserRepository();
    }

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getLikesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKE);
    }

    // --- CREATE ---

    public Task<Void> createUser(String id, String avatar, String name) {
        return mUserRepository.createUser(id,avatar,name);
    }

    public Task<Void> createLike(String id, String workmateId, String restaurantId) {
        return mUserRepository.createLike(id,workmateId,restaurantId);
    }

    // --- GET ---

    public com.google.android.gms.tasks.Task<DocumentSnapshot> getUser(String id) {
        return mUserRepository.getUser(id);
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public com.google.android.gms.tasks.Task<DocumentSnapshot> getLike(String id) {
        return mUserRepository.getLike(id);
    }

    // --- UPDATE ---

    public Task<Void> updateUserName(String id, String name) {
        return mUserRepository.updateUserName(id, name);
    }

    public Task<Void> updateUserAvatar(String id, String avatar) {
        return mUserRepository.updateUserAvatar(id, avatar);
    }

    public void updateUserRestaurant(String id, String restaurant) {
        mUserRepository.updateUserRestaurant(id, restaurant);
    }

    public void updateUserRestaurantAddress(String id, String restaurant_address) {
        mUserRepository.updateUserRestaurantAddress(id, restaurant_address);
    }

    public void updateUserRestaurantID(String id, String restaurantID) {
        mUserRepository.updateUserRestaurantID(id, restaurantID);
    }

    public void updateUserRestaurantDateChoice(String id, String restaurant_date_choice) {
        mUserRepository.updateUserRestaurantDateChoice(id, restaurant_date_choice);
    }

    public void updateUserLikeRestaurant(String id, Integer starNumber) {
        mUserRepository.updateUserLikeRestaurant(id, starNumber);
    }

    // --- DELETE ---

    public void deleteUser(String id) {
        mUserRepository.deleteUser(id);
    }

}