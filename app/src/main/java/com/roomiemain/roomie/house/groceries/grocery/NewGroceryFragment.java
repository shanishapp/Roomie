package com.roomiemain.roomie.house.groceries.grocery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.roomiemain.roomie.House;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.house.HouseActivityViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewGroceryFragment extends Fragment {

    private  NewGroceryFragmentViewModel newGroceryFragmentViewModel;
    private HouseActivityViewModel houseActivityViewModel;
    private House house;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText groceryName;
    private Button createGroceryButton;
    private NavController navController ;
    private String name = null;
    private int score = 1;
    private FrameLayout loadingOverlay;
    private FirebaseUser user;


    public NewGroceryFragment(){

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChoreFragment.
     */
    public static NewGroceryFragment newInstance(String param1, String param2) {
        return new NewGroceryFragment();
    }

    /* create view */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newGroceryFragmentViewModel = new ViewModelProvider(this).get(NewGroceryFragmentViewModel.class);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_grocery,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadCreateGroceryButton(view);
    }

    /* ui */
    private void loadCreateGroceryButton(View v) {
        createGroceryButton.setOnClickListener(view -> {
            if (view != null)
                doCreateGrocery(v);
        });
    }

    private void initViews(View view) {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();
        createGroceryButton = view.findViewById(R.id.createGroceryBtn);
        navController =  Navigation.findNavController(view);
        radioGroup = view.findViewById(R.id.grocerySizeRadioGroup);
        groceryName = view.findViewById(R.id.groceryNameEditText);
        loadingOverlay = view.findViewById(R.id.new_grocery_loading_overlay);
    }

    public void checkButton(){
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {

        });
    }

    /* create new grocery */

    private void doCreateGrocery(View view) {
        name = groceryName.getText().toString();
        if (name.equals("")){
            groceryName.setError("please enter grocery name");
            return;
        }

        LiveData<NewGroceryJob> job = newGroceryFragmentViewModel.createNewGrocery(house,name,1,user.getDisplayName());
        job.observe(getViewLifecycleOwner(), newGroceryJob -> {
            completeCreateGrocery(newGroceryJob);
        });
    }

    private void completeCreateGrocery(NewGroceryJob newGroceryJob) {
        switch (newGroceryJob.getJobStatus()){
            case IN_PROGRESS:
                createGroceryButton.setEnabled(false);
                break;
            case SUCCESS:
                navController.navigate(R.id.action_newGroceryFragment_to_house_groceries_fragment_dest);
                break;
            case ERROR:
                createGroceryButton.setEnabled(true);
                Toast.makeText(getContext(), getString(R.string.create_new_item_err_msg),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

}
