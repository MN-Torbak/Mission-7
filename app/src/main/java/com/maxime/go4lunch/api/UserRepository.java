package com.maxime.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.model.Like;
import com.maxime.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserRepository {


    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_LIKE = "likes";


    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void getUsersCollection(WorkmatesListener listener) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listener.onWorkmatesSuccess(convertWorkmates(task));
                } else {
                    Log.d("WorkmatesListener", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private List<Workmate> convertWorkmates(Task<QuerySnapshot> task) {
        List<Workmate> workmates = new ArrayList<>();
        for (DocumentSnapshot document : task.getResult()) {
            Workmate workmate = document.toObject(Workmate.class);
            if (workmate != null) {
                if (workmate.getRestaurant().equals("aucun")) {
                    workmates.add(workmates.size(), workmate);
                } else {
                    workmates.add(0, workmate);
                }
            }
        }

        return workmates;
    }

    public void getLikesCollection(LikesListener listener) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_LIKE);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listener.onLikesSuccess(getLikes(task));
                } else {
                    Log.d("LikesListener", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private List<Like> getLikes(Task<QuerySnapshot> task) {
        List<Like> likes = new ArrayList<>();
        for (DocumentSnapshot document : task.getResult()) {
            Like like = document.toObject(Like.class);
            assert like != null;
            likes.add(like);
        }
        return likes;
    }

    private CollectionReference getLikesCollection() {
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

    public void getUser(String id, OnUserSuccessListener listener) {
        Task<DocumentSnapshot> task = this.getUsersCollection().document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentWorkmate = documentSnapshot.toObject(Workmate.class);
                listener.onUserSuccess(currentWorkmate);
                }
            }
        );
    }

    public interface OnUserSuccessListener {
        void onUserSuccess(Workmate workmate);
    }

    public interface WorkmatesListener {
        void onWorkmatesSuccess(List<Workmate> workmates);
    }

    public interface LikesListener {
        void onLikesSuccess(List<Like> likes);
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

    public static com.google.android.libraries.places.api.model.DayOfWeek getCurrentDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 0:
                return com.google.android.libraries.places.api.model.DayOfWeek.MONDAY;
            case 1:
                return com.google.android.libraries.places.api.model.DayOfWeek.TUESDAY;
            case 2:
                return com.google.android.libraries.places.api.model.DayOfWeek.WEDNESDAY;
            case 3:
                return com.google.android.libraries.places.api.model.DayOfWeek.THURSDAY;
            case 4:
                return com.google.android.libraries.places.api.model.DayOfWeek.FRIDAY;
            case 5:
                return com.google.android.libraries.places.api.model.DayOfWeek.SATURDAY;
            case 6:
                return com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY;
            default:
                return com.google.android.libraries.places.api.model.DayOfWeek.SUNDAY;
        }
    }

}
