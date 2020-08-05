package com.example.roomie.house;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.BidiFormatter;
import android.widget.TextView;

import com.example.roomie.House;
import com.example.roomie.R;

public class HouseActivity extends AppCompatActivity {

    private HouseActivityViewModel houseActivityViewModel;

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        houseActivityViewModel = new ViewModelProvider(this).get(HouseActivityViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("house")) {
                House house = (House) extras.getSerializable("house");
                houseActivityViewModel.setHouse(house);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        setUIElements();
        setContent();
    }

    private void setUIElements() {
        title = findViewById(R.id.house_activity_title);
    }

    private void setContent() {
        BidiFormatter myBidiFormatter = BidiFormatter.getInstance();
        String houseName = houseActivityViewModel.getHouse().getName();
        title.setText(
                String.format(getString(R.string.house_activity_title),
                        myBidiFormatter.unicodeWrap(houseName))
        );
    }
}