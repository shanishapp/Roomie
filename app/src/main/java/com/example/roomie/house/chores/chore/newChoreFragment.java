package com.example.roomie.house.chores.chore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.House;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link newChoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class newChoreFragment extends Fragment {

    private NewChoreFragmentViewModel newChoreFragmentViewModel;
    private PowerSpinnerView titleSpinner;
    private EditText differentTitleEditText;
    private PowerSpinnerView assigneeSpinner;
    private Button setDateImageView;
    private TextView presentDateTextView;
    private HouseActivityViewModel houseActivityViewModel;
    private House house;
    private Button createChoreButton;
    private NavController navController ;
    private ArrayList<String> roommatesList;
    private Date date = null;
    private String title = null;
    private String assignee = null;


    public newChoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static newChoreFragment newInstance(String param1, String param2) {
        return new newChoreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newChoreFragmentViewModel = new ViewModelProvider(this).get(NewChoreFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_chore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        //set up add button
        createChoreButton.setOnClickListener(view1 -> {
            if (view1 != null) {
                doCreateNewChore(view);
            }
        });
    }

    private void initViews(View view) {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();

        createChoreButton = view.findViewById(R.id.createChoreBtn);
        navController = Navigation.findNavController(view);
        differentTitleEditText = view.findViewById(R.id.differentTitleEditText);
        titleSpinner = view.findViewById(R.id.titleSpinner) ;
        setTitleSpinner();
        assigneeSpinner = view.findViewById(R.id.assigneeSpinner);
        setAssigneeSpinner();
        setDateImageView = view.findViewById(R.id.setDueDateButton);
        presentDateTextView = view.findViewById(R.id.presentDateTextView);
        date = new Date();
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        presentDateTextView.setText(df.format(date));
        initAnimationListener();
    }

    private void initAnimationListener() {
       setDateImageView.setOnClickListener(view -> new SingleDateAndTimePickerDialog.Builder(getContext())
               .bottomSheet()
               .curved()
               .title("Pick a Date")
               .listener(date -> {
                   newChoreFragment.this.date = date;
                   String pattern = "dd/MM/yyyy HH:mm";
                   DateFormat df = new SimpleDateFormat(pattern);
                   presentDateTextView.setText(df.format(date));
               }).display());
    }

    private void setAssigneeSpinner() {
        String[] arr = {"shani","avihi","uri"};
        roommatesList = new ArrayList<String>(Arrays.asList(arr));
        roommatesList = getRoommies();
        assigneeSpinner.setItems(roommatesList);
        assigneeSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            assignee = s;
        });
        assigneeSpinner.setSpinnerOutsideTouchListener((view, motionEvent) -> assigneeSpinner.dismiss());
        assigneeSpinner.setLifecycleOwner(getViewLifecycleOwner());
    }

    private ArrayList<String> getRoommies() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Object[] arr = house.getRoomies().keySet().toArray();
        String[] stringArray = Arrays.asList(arr).toArray(new String[arr.length]);
        return new ArrayList<>(Arrays.asList(stringArray));
    }

    private void setTitleSpinner() {

        titleSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            titleSpinner.setError(null);
            title = s;
            if (s.equals(getString(R.string.other))) {
                differentTitleEditText.setVisibility(View.VISIBLE);
            } else {
                differentTitleEditText.setVisibility(View.GONE);
            }
        });

        titleSpinner.setSpinnerOutsideTouchListener((view, motionEvent) -> titleSpinner.dismiss());
        titleSpinner.setLifecycleOwner(getViewLifecycleOwner());

    }

    private void doCreateNewChore(View view) {
        if (title == null){
            TextView errorText = (TextView)titleSpinner;
            errorText.setError("");
            errorText.setText(R.string.no_title_error_msg);//changes the selected item text to this
            return; //TODO error massage
        } else if (title != null && title.equals(getString(R.string.other))) {
             String diffT = differentTitleEditText.getText().toString();
             if(! diffT.equals("")) {
                 title = diffT;
             }
        }

        LiveData<newChoreJob> job = newChoreFragmentViewModel.createNewChore(house,title,date,assignee);

        job.observe(getViewLifecycleOwner(), createNewChoreJob -> {
            switch (createNewChoreJob.getJobStatus()) {
                case IN_PROGRESS:
                    createChoreButton.setEnabled(false);
                    break;
                case SUCCESS:
                    navController.navigate(R.id.action_newChoreFragment_to_house_chores_fragment_dest);
                    break;
                case ERROR:
                    createChoreButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.create_new_chore_err_msg),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        });
    }
}