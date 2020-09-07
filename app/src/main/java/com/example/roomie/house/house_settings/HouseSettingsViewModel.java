package com.example.roomie.house.house_settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;

public class HouseSettingsViewModel extends ViewModel {


    public LiveData<GetHouseRoomiesJob> getRoomiesList(String houseId) {
        return HouseRepository.getInstance().getHouseRoomies(houseId);
    }

}
