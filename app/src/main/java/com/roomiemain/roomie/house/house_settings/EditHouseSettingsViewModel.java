package com.roomiemain.roomie.house.house_settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.repositories.HouseRepository;

public class EditHouseSettingsViewModel extends ViewModel {

    public LiveData<FirestoreJob> updateHouseInfo(String houseId, String name, String address, String desc) {
        return HouseRepository.getInstance().updateHouseInfo(houseId, name, address, desc);
    }

}
