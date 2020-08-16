package com.example.roomie.house;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.roomie.House;
import com.example.roomie.SignInActivity;
import com.example.roomie.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HouseActivity extends AppCompatActivity {

    private HouseActivityViewModel houseActivityViewModel;

    private AppBarConfiguration appBarConfiguration;

    private NavController navController;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private Toolbar toolbar;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        houseActivityViewModel = new ViewModelProvider(this).get(HouseActivityViewModel.class);

        // get data from other activities
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("house")) {
                House house = (House) extras.getSerializable("house");
                houseActivityViewModel.setHouse(house);
            }
        }

        setUIElements();
        setNavigation();
    }

    private void setUIElements() {
        toolbar = findViewById(R.id.house_toolbar);
        drawerLayout = findViewById(R.id.house_drawer_layout);
        navigationView = findViewById(R.id.house_nav_view);
        bottomNavigationView = findViewById(R.id.house_bottom_nav);
    }

    private void setNavigation() {
        navController = Navigation.findNavController(this, R.id.house_nav_host_fragment);
        setSupportActionBar(toolbar);

        // Add ids of menu items which we want to be top-level
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.house_feed_fragment_dest, R.id.house_feed_fragment_dest, R.id.house_user_profile_fragmnet_dest,
                R.id.house_settings_fragment_dest, R.id.house_chores_fragment_dest, R.id.house_groceries_fragment_dest,
                R.id.house_expenses_fragment_dest, R.id.house_invite_roomie_fragment_dest)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // show and hide bottom nav according to the destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.house_invite_roomie_fragment_dest:
                case R.id.house_settings_fragment_dest:
                case R.id.house_user_profile_fragmnet_dest:
                    bottomNavigationView.setVisibility(View.GONE);
                    break;
                default:
                    bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void signOut(MenuItem item) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
}