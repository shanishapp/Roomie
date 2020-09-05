package com.example.roomie.house.house_settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.GetHouseRoomiesJob;
import com.example.roomie.HouseRepository;

public class HouseSettingsViewModel extends ViewModel {


    public LiveData<GetHouseRoomiesJob> getRoomiesList(String houseId) {
        return HouseRepository.getInstance().getHouseRoomies(houseId);
    }

}
