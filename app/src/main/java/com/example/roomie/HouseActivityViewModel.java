package com.example.roomie;

import androidx.lifecycle.ViewModel;

public class HouseActivityViewModel extends ViewModel {

    private House house;


    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
