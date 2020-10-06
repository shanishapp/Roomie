package com.roomiemain.roomie.house.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roomiemain.roomie.repositories.GetMessagesJob;
import com.roomiemain.roomie.repositories.HouseRepository;
import com.roomiemain.roomie.repositories.SendMessageJob;
import com.google.firebase.auth.FirebaseAuth;

public class HouseChatViewModel extends ViewModel {

    private String houseId;

    private FirebaseAuth auth;


    public HouseChatViewModel() {
        auth = FirebaseAuth.getInstance();
    }


    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public LiveData<SendMessageJob> sendMessage(String messageContent) {
        return HouseRepository.getInstance().sendMessage(houseId, auth.getCurrentUser().getUid(), messageContent);
    }

    public LiveData<GetMessagesJob> getMessages() {
        return HouseRepository.getInstance().getMessages(houseId);
    }

    public LiveData<GetMessagesJob> listenForMessages() {
        return HouseRepository.getInstance().listenForMessages(houseId);
    }

    public void stopListeningForMessages() {
        HouseRepository.getInstance().stopListeningForMessages();
    }

}
