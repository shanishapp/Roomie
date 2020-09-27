package com.example.roomie.house;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.House;
import com.example.roomie.User;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;

import java.util.List;

public class HouseActivityViewModel extends ViewModel {

    private House house;

    private List<User> roomiesList;

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    /**
     * Loads the roomies list from firestore.
     * @return A job for loading the roomies list.
     */
    public LiveData<GetHouseRoomiesJob> loadHouseRoomies() {
        return HouseRepository.getInstance().getHouseRoomies(house.getId());
    }

    /**
     * Returns the loaded roomies list. Should be called after loadHouseRoomies has finished.
     * @return The list of roomies.
     */
    public List<User> getRoomiesList() {
        return roomiesList;
    }

    public void setRoomiesList(List<User> roomiesList) {
        this.roomiesList = roomiesList;
    }

    public String getRoomieNameById(String uid) {
        for (User curUser : roomiesList) {
            if (curUser.getUid().equals(uid)) {
                return curUser.getUsername();
            }
        }

        return "";
    }

    public int getRoomieIndexById(String uid) {
        for (int i = 0; i < roomiesList.size(); i++) {
            if (roomiesList.get(i).getUid().equals(uid)) {
                return i;
            }
        }

        return -1;
    }
}
