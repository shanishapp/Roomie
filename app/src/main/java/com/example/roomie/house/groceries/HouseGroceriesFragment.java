package com.example.roomie.house.groceries;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.roomie.FirestoreJob;
import com.example.roomie.LinearLayoutManagerWithSmoothScroller;
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
    LinearLayoutManager layoutManager;
    private GroceryAdapter adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private HouseGroceriesFragmentViewModel vm;
    private ArrayList<Grocery> groceryList;
    private MovableFloatingActionButton addChoreButton;
    private MovableFloatingActionButton moveToExpensesBtn;
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
        recyclerView = v.findViewById(R.id.groceriesRecyclerView);
        layoutManager = new LinearLayoutManagerWithSmoothScroller(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        groceryList = vm.sortGroceries(groceryList);
        groceryList = vm.addDates(groceryList);
        adapter = new GroceryAdapter(groceryList, HouseGroceriesFragment.this);
        recyclerView.setAdapter(adapter);
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
        } else {
            loadingOverlay.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onGroceryLongClick(int pos) {
        Toast.makeText(getContext(),"long clicked",Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(getContext())
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        vm.deleteGroceryForever(groceryList.get(pos),houseActivityViewModel.getHouse().getId());
                        adapter.notifyDataSetChanged();
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        return true;
    }

    @Override
    public void onGroceryPicked(Grocery grocery) {
        pickedGroceries.add(grocery);
        Toast.makeText(getContext(),grocery.get_name()+" added to expenses",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGroceryUnPicked(Grocery grocery) {
        pickedGroceries.remove(grocery);
        Toast.makeText(getContext(),grocery.get_name()+" removed from expenses",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        addChoreButton = view.findViewById(R.id.fab);
        moveToExpensesBtn = view.findViewById(R.id.addToExpensesBtn);
        pickedGroceries = new ArrayList<>();
        //set up add button
        addChoreButton.setOnClickListener(view1 -> {
            if(view1 != null){
                navController.navigate(R.id.action_house_groceries_fragment_dest_to_newGroceryFragment);
            }
        });

        moveToExpensesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Add to expenses")
                        .setMessage("you have "+pickedGroceries.size()+ " chosen groceries\nwould you like to combine them to one expense?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            //addToExpenses(pickedGroceries); //TODO talk with uri
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        loadingOverlay = view.findViewById(R.id.groceries_loading_overlay);
        toggleLoadingOverlay(true);
    }

}