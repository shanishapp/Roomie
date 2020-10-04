package com.example.roomie.house.user_profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.expenses.Expense;
import com.example.roomie.repositories.GetChoresJob;
import com.example.roomie.repositories.GetExpensesJob;
import com.example.roomie.repositories.HouseRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserProfileViewModel extends ViewModel {

    private FirebaseAuth auth;

    private FirebaseUser user;

    private MutableLiveData<String> userName;

    private MutableLiveData<String> userEmail;

    private MutableLiveData<Uri> userProfilePicture;

    private MutableLiveData<Integer> userBrooms;

    private MutableLiveData<Integer> userChores;

    private MutableLiveData<Float> userExpenses;

    private List<Chore> doneChoreList;

    private List<Chore> undoneChoreList;

    private List<Expense> expenseList;


    public UserProfileViewModel() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        userProfilePicture = new MutableLiveData<>();
        userBrooms = new MutableLiveData<>();
        userChores = new MutableLiveData<>();
        userExpenses = new MutableLiveData<>();
    }


    public LiveData<String> getUserName() {
        userName.setValue(user.getDisplayName());
        return userName;
    }

    public LiveData<String> getUserEmail() {
        userEmail.setValue(user.getEmail());
        return userEmail;
    }

    public LiveData<Uri> getUserProfilePicture() {
        userProfilePicture.setValue(user.getPhotoUrl());
        return userProfilePicture;
    }

    public LiveData<Integer> getUserBrooms() {
        userBrooms.setValue(0);
        return userBrooms;
    }

    public LiveData<Integer> getUserChores() {
        userChores.setValue(0);
        return userChores;
    }

    public LiveData<Float> getUserExpenses() {
        userExpenses.setValue(0.0f);
        return userExpenses;
    }

    public List<Chore> getDoneChoreList() {
        return doneChoreList;
    }

    public List<Chore> getUndoneChoreList() {
        return undoneChoreList;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }

    public void setUserBrooms(int userBrooms) {
        this.userBrooms.setValue(userBrooms);
    }

    public void setUserChores(int userChores) {
        this.userChores.setValue(userChores);
    }

    public void setDoneChoreList(List<Chore> doneChoreList) {
        this.doneChoreList = doneChoreList;
        if (doneChoreList == null) {
            this.userBrooms.setValue(0);
        } else {
            int brooms = 0;
            for (Chore chore : doneChoreList) {
                brooms += chore.get_score();
            }
            this.userBrooms.setValue(brooms);
        }
    }

    public void setUndoneChoreList(List<Chore> undoneChoreList) {
        this.undoneChoreList = undoneChoreList;
        if (undoneChoreList == null) {
            this.userChores.setValue(0);
        } else {
            this.userChores.setValue(undoneChoreList.size());
        }
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        if (expenseList == null) {
            this.userExpenses.setValue(0.0f);
        } else {
            float expenseSum = 0.0f;
            for (Expense expense : expenseList) {
                expenseSum += expense.get_cost();
            }
            this.userExpenses.setValue(expenseSum);
        }
    }

    public LiveData<GetChoresJob> loadDoneChores(String houseId) {
        return HouseRepository.getInstance().getChoresByParameters(houseId, auth.getCurrentUser().getDisplayName(), true);
    }

    public LiveData<GetChoresJob> loadUndoneChores(String houseId) {
        return HouseRepository.getInstance().getChoresByParameters(houseId, auth.getCurrentUser().getDisplayName(), false);
    }

    public LiveData<GetExpensesJob> loadExpenses(String houseId) {
        return HouseRepository.getInstance().getExpensesByParameters(houseId, auth.getCurrentUser().getUid());
    }
}
