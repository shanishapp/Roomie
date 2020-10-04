package com.example.roomie.house;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.expenses.Expense;
import com.example.roomie.repositories.GetChoresJob;
import com.example.roomie.repositories.GetExpensesJob;
import com.example.roomie.repositories.GetUserJob;
import com.example.roomie.repositories.HouseRepository;
import com.example.roomie.repositories.UserRepository;

import java.util.List;

public class RoomieProfileViewModel extends ViewModel {

    private MutableLiveData<String> roomieName;

    private MutableLiveData<String> roomieEmail;

    private MutableLiveData<String> roomieProfilePicture;

    private MutableLiveData<Integer> roomieBrooms;

    private MutableLiveData<Integer> roomieChores;

    private MutableLiveData<Float> roomieExpenses;

    private List<Chore> doneChoreList;

    private List<Chore> undoneChoreList;

    private List<Expense> expenseList;


    public RoomieProfileViewModel() {
        roomieName = new MutableLiveData<>();
        roomieEmail = new MutableLiveData<>();
        roomieProfilePicture = new MutableLiveData<>();
        roomieBrooms = new MutableLiveData<>();
        roomieChores = new MutableLiveData<>();
        roomieExpenses = new MutableLiveData<>();
    }


    public LiveData<GetUserJob> getRoomie(String roomieId) {
        return UserRepository.getInstance().getUserById(roomieId);
    }

    public LiveData<String> getUserName() {
        roomieName.setValue("");
        return roomieName;
    }

    public LiveData<String> getUserEmail() {
        roomieEmail.setValue("");
        return roomieEmail;
    }

    public LiveData<String> getUserProfilePicture() {
        roomieProfilePicture.setValue(null);
        return roomieProfilePicture;
    }

    public LiveData<Integer> getUserBrooms() {
        roomieBrooms.setValue(0);
        return roomieBrooms;
    }

    public LiveData<Integer> getUserChores() {
        roomieChores.setValue(0);
        return roomieChores;
    }

    public LiveData<Float> getUserExpenses() {
        roomieExpenses.setValue(0.0f);
        return roomieExpenses;
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

    public void setRoomieName(String roomieName) {
        this.roomieName.setValue(roomieName);
    }

    public void setRoomieEmail(String roomieEmail) {
        this.roomieEmail.setValue(roomieEmail);
    }

    public void setRoomieProfilePicture(String profilePicture) {
        this.roomieProfilePicture.setValue(profilePicture);
    }

    public void setUserBrooms(int userBrooms) {
        this.roomieBrooms.setValue(userBrooms);
    }

    public void setUserChores(int userChores) {
        this.roomieChores.setValue(userChores);
    }

    public void setRoomieExpenses(Float roomieExpenses) {
        this.roomieExpenses.setValue(roomieExpenses);
    }

    public void setDoneChoreList(List<Chore> doneChoreList) {
        this.doneChoreList = doneChoreList;
        if (doneChoreList == null) {
            this.roomieBrooms.setValue(0);
        } else {
            int brooms = 0;
            for (Chore chore : doneChoreList) {
                brooms += chore.get_score();
            }
            this.roomieBrooms.setValue(brooms);
        }
    }

    public void setUndoneChoreList(List<Chore> undoneChoreList) {
        this.undoneChoreList = undoneChoreList;
        if (undoneChoreList == null) {
            this.roomieChores.setValue(0);
        } else {
            this.roomieChores.setValue(undoneChoreList.size());
        }
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
        if (expenseList == null) {
            this.roomieExpenses.setValue(0.0f);
        } else {
            float expenseSum = 0.0f;
            for (Expense expense : expenseList) {
                expenseSum += expense.get_cost();
            }
            this.roomieExpenses.setValue(expenseSum);
        }
    }

    public LiveData<GetChoresJob> loadDoneChores(String houseId, String username) {
        return HouseRepository.getInstance().getChoresByParameters(houseId, username, true);
    }

    public LiveData<GetChoresJob> loadUndoneChores(String houseId, String username) {
        return HouseRepository.getInstance().getChoresByParameters(houseId, username, false);
    }

    public LiveData<GetExpensesJob> loadExpenses(String houseId, String uid) {
        return HouseRepository.getInstance().getExpensesByParameters(houseId, uid);
    }

}
