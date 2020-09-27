package com.example.roomie.house.groceries;

import android.app.AlertDialog;
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
import com.example.roomie.house.expenses.Expense;
import com.example.roomie.house.expenses.NewExpenseViewModel;
import com.example.roomie.house.groceries.grocery.Grocery;
import com.example.roomie.house.groceries.grocery.NewGroceryJob;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    private FirebaseUser user;

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
        user = FirebaseAuth.getInstance().getCurrentUser();
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

        if(groceryList.size() > 0){
            groceryList = vm.sortGroceries(groceryList);
            groceryList = vm.addDates(groceryList);
        }
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
    public boolean onGroceryClick(int pos, CustomCheckBox pickGroceryCheckBox) {
        Grocery grocery = groceryList.get(pos);
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        new AlertDialog.Builder(getContext())
                .setTitle(grocery.get_name())
                .setMessage(getString(R.string.created_at)+" "+df.format(grocery.get_creationDate()))
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    // Continue with delete operation
                    LiveData<NewGroceryJob> job = vm.deleteGroceryForever(groceryList.get(pos), houseActivityViewModel.getHouse().getId());
                    job.observe(getViewLifecycleOwner(), newGroceryJob -> {
                        if(newGroceryJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                            groceryList.remove(pos);
                            adapter.notifyDataSetChanged();
                        }
                    });
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(getString(R.string.check), (dialogInterface, i) -> {
                    pickGroceryCheckBox.setChecked(!pickGroceryCheckBox.isChecked());
                })
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
                            StringBuilder description = new StringBuilder();
                            for(Grocery newGrocery:pickedGroceries){
                                description.append(newGrocery.get_name()).append("\n");
                            }
                            NewExpenseViewModel expenseViewModel =  new ViewModelProvider(requireActivity()).get(NewExpenseViewModel.class);
                            expenseViewModel.createNewExpense(houseActivityViewModel.getHouse(),
                                    getString(R.string.house_bottom_menu_groceries),description.toString(),
                                    0,user.getUid(),"Shani Shapp", Expense.ExpenseType.GROCERIES,new Date());
                            for(Grocery grocery: pickedGroceries){
                                vm.deleteGroceryForever(grocery,houseActivityViewModel.getHouse().getId());
                                groceryList.remove(grocery);
                            }
                            pickedGroceries.clear();
                            adapter.notifyDataSetChanged();
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