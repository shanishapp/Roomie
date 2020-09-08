package com.example.roomie.house;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.repositories.GetUserJob;
import com.example.roomie.repositories.UserRepository;

public class RoomieProfileViewModel extends ViewModel {

    public LiveData<GetUserJob> getRoomie(String roomieId) {
        return UserRepository.getInstance().getUserById(roomieId);
    }

}
