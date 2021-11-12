package com.maxime.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.maxime.go4lunch.model.Workmate;

import java.util.List;

public class UserManager {

    UserRepository mUserRepository;

    public UserManager() {
        mUserRepository = new UserRepository();
    }

    // --- COLLECTION REFERENCE ---

    public void getUsersCollection(UserRepository.WorkmatesListener listener) {
        mUserRepository.getUsersCollection(listener);
    }

    public void getLikesCollection(UserRepository.LikesListener listener) {
        mUserRepository.getLikesCollection(listener);
    }

    // --- CREATE ---

    public Task<Void> createUser(String id, String avatar, String name) {
        return mUserRepository.createUser(id, avatar, name);
    }

    public void createLike(String id, String workmateId, String restaurantId) {
        mUserRepository.createLike(id, workmateId, restaurantId);
    }

    // --- GET ---

    public void getUser(String id, UserRepository.OnUserSuccessListener listener) {
        mUserRepository.getUser(id, listener);
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
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

    private void orderWorkmates(UserRepository.WorkmatesListener workmatesListener) {
        mUserRepository.getUsersCollection(new UserRepository.WorkmatesListener() {
            @Override
            public void onWorkmatesSuccess(List<Workmate> workmates) {
                for (Workmate workmate : workmates) {
                    if (workmate.getRestaurant().equals("aucun")) {
                        workmates.add(workmates.size(), workmate);
                    } else {
                        workmates.add(0, workmate);
                    }
                }
            }
        });

    }

    // --- DELETE ---

    public void deleteUser(String id) {
        mUserRepository.deleteUser(id);
    }

}