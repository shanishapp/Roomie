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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.roomie.House;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;


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
    private EditText differentTitleEditTText;
    private EditText contentEditText;
    private PowerSpinnerView assigneeSpinner;
    private LottieAnimationView snoozeAnimationView;
    private TextView presentDateTextView;
    private HouseActivityViewModel houseActivityViewModel;
    private House house;
    private Button createChoreButton;
    private NavController navController ;
    private ArrayList<String> roommatesList;


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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chore, container, false);



        //define behaviour for the date picker button
        return v;

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

        differentTitleEditTText = view.findViewById(R.id.differentTitleEditText);
        titleSpinner = view.findViewById(R.id.titleSpinner) ;
        setTitleSpinner();

        assigneeSpinner = view.findViewById(R.id.assigneeSpinner);
        setAssigneeSpinner();

        contentEditText = view.findViewById(R.id.contentEditText);
        snoozeAnimationView = view.findViewById(R.id.snoozeAnimationView);
        presentDateTextView = view.findViewById(R.id.presentDateTextView);
    }

    private void setAssigneeSpinner() {
        String[] arr = {"shani","avihi","uri"};
        roommatesList = new ArrayList<String>(Arrays.asList(arr));
        //ArrayAdapter<String> arr = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, stringArray);
        assigneeSpinner.setItems(roommatesList);
    }

    private void setTitleSpinner() {
//        ArrayAdapter<String> arr = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.default_titles));
//        titleSpinner.setAdapter(arr);
        titleSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int i, String s) {


                if (s.equals("other") || s.equals("אחר")) {
                    differentTitleEditTText.setVisibility(View.VISIBLE);
                } else {
                    differentTitleEditTText.setText(s);
                }
            }
        });

    }

    private void doCreateNewChore(View view) {
        String title, content, assignee;
        Date dueDate;
        title = differentTitleEditTText.getText().toString();
        if (title == null) return;
        content = contentEditText.getText().toString();
        int idx =  assigneeSpinner.getSelectedIndex();
        assignee = roommatesList.get(idx);
        dueDate = new Date();

        LiveData<newChoreJob> job = newChoreFragmentViewModel.createNewChore(house,title,content,dueDate,assignee);

        job.observe(getViewLifecycleOwner(), createNewChoreJob -> {
            switch (createNewChoreJob.getJobStatus()) {
                case IN_PROGRESS:
                    createChoreButton.setEnabled(false);
                    break;
                case SUCCESS:
                    navController.navigate(R.id.action_choreFragment_to_house_chores_fragment_dest);
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

//    private String getTitle() {
//        String title;
//        if (titleSpinner.getSelectedItem().toString().equals("other")) { // TODO, switch to string resource
//            title = differentTitleEditTText.getText().toString();
//            if(title.equals("")) {// TODO nul ??
//                differentTitleEditTText.setError("you need to enter title"); // TODO string resource
//                return null;
//            }
//        } else {
//            title = titleSpinner.getSpinner().getSelectedItem().toString();
//        }
//        return title;
//    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            // TODO something with the date. This Date object contains
            // the date and time that the user has selected.
        }

        @Override
        public void onDateTimeCancel()
        {
            // Overriding onDateTimeCancel() is optional.
        }
    };
}