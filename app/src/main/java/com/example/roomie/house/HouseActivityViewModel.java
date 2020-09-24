package com.example.roomie.house;

import androidx.lifecycle.ViewModel;

import com.example.roomie.House;

public class HouseActivityViewModel extends ViewModel {

    private House house;

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
