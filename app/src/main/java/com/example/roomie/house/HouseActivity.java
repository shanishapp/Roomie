package com.example.roomie.house;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.roomie.House;
import com.example.roomie.SignInActivity;
import com.example.roomie.R;
import com.example.roomie.house.chores.chore.ChoreFragment;
import com.example.roomie.house.chores.chore.NewChoreFragment;
import com.example.roomie.repositories.GetHouseRoomiesJob;
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
        loadContent();
    }

    private void loadContent() {
        LiveData<GetHouseRoomiesJob> job = houseActivityViewModel.loadHouseRoomies();
        job.observe(this, getHouseRoomiesJob -> {
            switch (getHouseRoomiesJob.getJobStatus()) {
                case SUCCESS:
                    houseActivityViewModel.setRoomiesList(getHouseRoomiesJob.getRoomiesList());
                    break;
                case ERROR:
                    Toast.makeText(this, "Error loading roomies.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
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
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }        // Add ids of menu items which we want to be top-level
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.house_feed_fragment_dest, R.id.house_feed_fragment_dest, R.id.house_user_profile_fragment_dest,
                R.id.house_settings_fragment_dest, R.id.house_chores_fragment_dest, R.id.house_groceries_fragment_dest,
                R.id.house_expenses_fragment_dest, R.id.house_invite_roomie_fragment_dest, R.id.house_chat_fragment_dest)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // show and hide bottom nav according to the destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.house_feed_fragment_dest:
                case R.id.house_chores_fragment_dest:
                case R.id.house_groceries_fragment_dest:
                case R.id.house_expenses_fragment_dest:
                case R.id.house_chat_fragment_dest:
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    break;
                default:
                    bottomNavigationView.setVisibility(View.GONE);
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.house_nav_host_fragment);
        Fragment f = fragment == null ? null : fragment.getChildFragmentManager().getFragments().get(0);

        if ((f instanceof IOnBackPressed)) {
            if( ((IOnBackPressed) f).onBackPressed()) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }//TODO MAYBE NOT NEEDED
                return;
            }
        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.house_nav_host_fragment);
        Fragment f = fragment == null ? null : fragment.getChildFragmentManager().getFragments().get(0);
        if (f.getClass().equals(NewChoreFragment.class)) {
            ((NewChoreFragment) f).onBackPressed();
            return true;
        } else if (f.getClass().equals(ChoreFragment.class)){
            if(((ChoreFragment) f).onBackPressed())
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public interface IOnBackPressed {
        /**
         * If you return true the back press will not be taken into account, otherwise the activity will act naturally
         * @return true if your processing has priority if not false
         */
        boolean onBackPressed();
    }




}