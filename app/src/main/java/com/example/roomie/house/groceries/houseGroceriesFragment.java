package com.example.roomie.house.groceries;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.roomie.FirestoreJob;
import com.example.roomie.MovableFloatingActionButton;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.house.groceries.grocery.Grocery;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseGroceriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseGroceriesFragment extends Fragment implements GroceryAdapter.OnGroceryListener{

    private RecyclerView recyclerView;
    private GroceryAdapter adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private HouseGroceriesFragmentViewModel vm;
    private ArrayList<Grocery> groceryList;
    private MovableFloatingActionButton button;
    private NavController navController;
    private FrameLayout loadingOverlay;
    private ArrayList<Grocery> pickedGroceries;

    public HouseGroceriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment houseGroceriesFragment.
     */
    public static HouseGroceriesFragment newInstance() {
        return new HouseGroceriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_house_groceries, container, false);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        vm = new ViewModelProvider(requireActivity()).get(HouseGroceriesFragmentViewModel.class);
        LiveData<AllGroceriesJob> job = vm.getAllGroceries(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allGroceriesJob -> {
            if(allGroceriesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                groceryList = (ArrayList<Grocery>) allGroceriesJob.getGroceryList();
                toggleLoadingOverlay(false);
                setRecyclerView(v);
            }
        });
        return v;
    }

    private void setRecyclerView(View v) {
        adapter = new GroceryAdapter(groceryList, HouseGroceriesFragment.this);
        RecyclerView recyclerView = v.findViewById(R.id.groceriesRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
        } else {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGroceryClick(int pos) {

    }

    @Override
    public void onGroceryPicked(Grocery grocery) {
        groceryList.add(grocery);
        Toast.makeText(getContext(),grocery.get_name()+" added to expenses",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGroceryUnPicked(Grocery grocery) {
        groceryList.remove(grocery);
        Toast.makeText(getContext(),grocery.get_name()+" removed from expenses",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        button = view.findViewById(R.id.fab);
        pickedGroceries = new ArrayList<>();
        //set up add button
        button.setOnClickListener(view1 -> {
            if(view1 != null){
                navController.navigate(R.id.action_house_groceries_fragment_dest_to_newGroceryFragment);
            }
        });
        loadingOverlay = view.findViewById(R.id.groceries_loading_overlay);
        toggleLoadingOverlay(true);
    }

}