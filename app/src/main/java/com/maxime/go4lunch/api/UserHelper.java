package com.maxime.go4lunch.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maxime.go4lunch.model.Workmate;

import static android.content.ContentValues.TAG;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static com.google.android.gms.tasks.Task<Void> createUser(String id, String avatar, String name) {
        Workmate userToCreate = new Workmate(id, avatar, name);
        return UserHelper.getUsersCollection().document(id).set(userToCreate);
    }

    // --- GET ---

    public static com.google.android.gms.tasks.Task<DocumentSnapshot> getUser(String id){
        return UserHelper.getUsersCollection().document(id).get();
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

    // --- DELETE ---

    public static Task<Void> deleteUser(String id) {
        return UserHelper.getUsersCollection().document(id).delete();
    }

}