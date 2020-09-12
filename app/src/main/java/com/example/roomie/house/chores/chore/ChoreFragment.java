package com.example.roomie.house.chores.chore;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.House;
import com.example.roomie.R;
import com.example.roomie.User;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.house.chores.HouseChoresFragmentViewModel;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChoreFragment extends Fragment {

    private HouseChoresFragmentViewModel choreFragmentViewModel;
    private TextView presentDateTextView;
    private TextView presentAssigneeTextView;
    private TextView presentTitleTextView;
    private HouseActivityViewModel houseActivityViewModel;
    private House house;
    private ImageButton editTitleBtn;
    private ImageButton editAssigneeBtn;
    private ImageButton editDueDateBtn;
    private NavController navController ;
    private ArrayList<String> roommatesList;
    private Chore chore;
    private String choreId;
    private String title;
    private String dueDate;
    private String assignee;
    private FrameLayout loadingOverlay;
    private PowerSpinnerView assigneeSpinner;


    public ChoreFragment() {
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
    public static ChoreFragment newInstance(String param1, String param2) {
        return new ChoreFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choreFragmentViewModel = new ViewModelProvider(this).get(HouseChoresFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setButtons();
        toggleLoadingOverlay(true);
        loadRoommies(view);
    }

    private void setButtons() {
        editTitleBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            AlertDialog alertDialog = builder.create();
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_change_title,null);
            Button changeBtn = customLayout.findViewById(R.id.changeTitleBtn);
            PowerSpinnerView titleSpinner = customLayout.findViewById(R.id.newTitleSpinner);
            EditText editText = customLayout.findViewById(R.id.differentNewTitleEditText);
            setTitleSpinner(titleSpinner,editText);
            alertDialog.setView(customLayout);
            changeBtn.setOnClickListener((v) -> {
                if (title == null){
                    TextView errorText = (TextView)titleSpinner;
                    errorText.setError("");
                    errorText.setText(R.string.no_title_error_msg);//changes the selected item text to this
                    return; //TODO error massage
                } else if (title != null && title.equals(getString(R.string.other))) {
                    String diffT = editText.getText().toString();
                    if(! diffT.equals("")) {
                        title = diffT;
                    }
                }

                LiveData<newChoreJob> job = choreFragmentViewModel.setTitle(choreId, title, house.getId()); //TODO livedata

                job.observe(getViewLifecycleOwner(), createNewChoreJob -> {
                    switch (createNewChoreJob.getJobStatus()) {
                        case IN_PROGRESS:
                            //createChoreButton.setEnabled(false);
                            break;
                        case SUCCESS:
                            //navController.navigate(R.id.action_newChoreFragment_to_house_chores_fragment_dest);
                            presentTitleTextView.setText(ChoreFragment.this.title);
                            break;
                        case ERROR:
                            //createChoreButton.setEnabled(true);
                            Toast.makeText(getContext(), getString(R.string.create_new_item_err_msg),
                                    Toast.LENGTH_LONG).show();
                            break;
                        default:
                    }
                });
                alertDialog.cancel();
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alertDialog.show();
        });
        editAssigneeBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            AlertDialog alertDialog = builder.create();
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_change_assignee,null);
            Button changeBtn = customLayout.findViewById(R.id.changeAssigneeBtn);
            assigneeSpinner = customLayout.findViewById(R.id.newAssigneeSpinner);
            setAssigneeSpinner(assigneeSpinner);
            alertDialog.setView(customLayout);
            changeBtn.setOnClickListener((v) -> {
                choreFragmentViewModel.setAssignee(choreId,ChoreFragment.this.assignee,house.getId());
                presentAssigneeTextView.setText(ChoreFragment.this.assignee);
                alertDialog.cancel();

            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alertDialog.show();
        });

        editDueDateBtn.setOnClickListener(view -> new SingleDateAndTimePickerDialog.Builder(getContext())
                .curved()
                .title("Pick a Date")
                .mainColor(ContextCompat.getColor(getContext(), R.color.buttonColor))
                .listener(date -> {
                    String pattern = "dd/MM/yyyy HH:mm";
                    DateFormat df = new SimpleDateFormat(pattern);
                    ChoreFragment.this.dueDate = df.format(date);
                    choreFragmentViewModel.setDueDate(choreId,ChoreFragment.this.dueDate,house.getId());
                    presentDateTextView.setText(ChoreFragment.this.dueDate);
                }).display());
    }

    private void setAssigneeSpinner(PowerSpinnerView assigneeSpinner) {
        assigneeSpinner.setItems(roommatesList);
        assigneeSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            assignee = s;
        });
        assigneeSpinner.setSpinnerOutsideTouchListener((view, motionEvent) -> assigneeSpinner.dismiss());
        assigneeSpinner.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setTitleSpinner(PowerSpinnerView titleSpinner, EditText differentTitleEditText) {

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

    private void initViews(View view) {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();
        navController = Navigation.findNavController(view);
        choreId = getArguments().getString("choreId","");
        title = getArguments().getString("choreTitle","");
        dueDate = getArguments().getString("choreDueDate","1111");
        assignee = getArguments().getString("choreAssignee","");

        editTitleBtn = view.findViewById(R.id.editTitleBtn);
        editAssigneeBtn = view.findViewById(R.id.editAssigneeBtn);
        editDueDateBtn= view.findViewById(R.id.editDueDateBtn);

        presentDateTextView = view.findViewById(R.id.presentDateTextView);
        presentAssigneeTextView = view.findViewById(R.id.presentAssigneeTextView);
        presentTitleTextView = view.findViewById(R.id.presentTitleTextView);
        presentDateTextView.setText(dueDate);
        presentAssigneeTextView.setText(assignee);
        presentTitleTextView.setText(title);
        roommatesList = new ArrayList<>();
        loadingOverlay = view.findViewById(R.id.chore_loading_overlay);

    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            editAssigneeBtn.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            editAssigneeBtn.setEnabled(true);
        }
    }

    private void loadRoommies(View view) {
        LiveData<GetHouseRoomiesJob> job = HouseRepository.getInstance().getHouseRoomies(house.getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            switch (getHouseRoomiesJob.getJobStatus()){
                case SUCCESS:
                    for (User user: getHouseRoomiesJob.getRoomiesList()){
                        roommatesList.add(user.getUsername());
                    }
                    editAssigneeBtn.setEnabled(true);
                    toggleLoadingOverlay(false);
            }
        });
    }
}