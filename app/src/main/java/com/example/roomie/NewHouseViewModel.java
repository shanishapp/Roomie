package com.example.roomie;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class NewHouseViewModel extends ViewModel {

    private final static String HOUSES_COLLECTION_NAME = "houses";

    private FirebaseFirestore db;

    public NewHouseViewModel() {
        db = FirebaseFirestore.getInstance();
    }

    public void createNewHouse(String houseName, String houseAddress, String houseDesc) {

    }

}
