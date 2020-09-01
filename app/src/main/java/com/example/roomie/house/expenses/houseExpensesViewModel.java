package com.example.roomie.house.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class houseExpensesViewModel extends ViewModel
{
    private FirebaseFirestore db;
    private MutableLiveData<List<Expense>> expenses;
    public ExpenseAdapter expenseAdapter = null;

    public houseExpensesViewModel()
    {
        db = FirebaseFirestore.getInstance();
        expenses = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<CreateNewExpenseJob> setRoomie(String expenseId, String roomie, String houseId)
    {
        //TODO: only started
        return null;
    }
}
