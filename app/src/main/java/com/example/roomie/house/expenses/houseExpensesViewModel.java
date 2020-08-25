package com.example.roomie.house.expenses;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class houseExpensesViewModel extends ViewModel
{
    private FirebaseFirestore db;

    public houseExpensesViewModel()
    {
        db = FirebaseFirestore.getInstance();
    }

}
