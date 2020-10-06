package com.roomiemain.roomie.house.house_settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.repositories.GetHouseRoomiesJob;
import com.roomiemain.roomie.repositories.HouseRepository;

public class HouseSettingsViewModel extends ViewModel {


    public LiveData<GetHouseRoomiesJob> getRoomiesList(String houseId) {
        return HouseRepository.getInstance().getHouseRoomies(houseId);
    }

}
