package com.roomiemain.roomie.house.chores.chore;

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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.House;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.User;
import com.roomiemain.roomie.house.HouseActivity;
import com.roomiemain.roomie.house.HouseActivityViewModel;
import com.roomiemain.roomie.house.chores.HouseChoresFragmentViewModel;
import com.roomiemain.roomie.repositories.GetHouseRoomiesJob;
import com.roomiemain.roomie.repositories.HouseRepository;
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
public class ChoreFragment extends Fragment implements HouseActivity.IOnBackPressed {

    private HouseChoresFragmentViewModel choreFragmentViewModel;
    private TextView presentDateTextView;
    private TextView presentAssigneeTextView;
    private TextView presentTitleTextView;
    private TextView presetContentTextView;
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
    private String content;
    private FrameLayout loadingOverlay;
    private PowerSpinnerView assigneeSpinner;
    private ImageButton editContentBtn;


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


    // content initialization
    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            editAssigneeBtn.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            editAssigneeBtn.setEnabled(true);
        }
    }

    private void initViews(View view) {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();
        navController = Navigation.findNavController(view);
        choreId = getArguments().getString("choreId","");
        title = getArguments().getString("choreTitle","");
        dueDate = getArguments().getString("choreDueDate","1111");
        assignee = getArguments().getString("choreAssignee","");
        content = getArguments().getString("choreContent","");

        editTitleBtn = view.findViewById(R.id.editTitleBtn);
        editAssigneeBtn = view.findViewById(R.id.editAssigneeBtn);
        editDueDateBtn= view.findViewById(R.id.editDueDateBtn);
        editContentBtn = view.findViewById(R.id.editContentBtn);

        presentDateTextView = view.findViewById(R.id.presentDateTextView);
        presentAssigneeTextView = view.findViewById(R.id.presentAssigneeTextView);
        presentTitleTextView = view.findViewById(R.id.presentTitleTextView);
        presetContentTextView = view.findViewById(R.id.presentContentTextView);
        presentDateTextView.setText(dueDate);
        presentAssigneeTextView.setText(assignee);
        presentTitleTextView.setText(title);
        presetContentTextView.setText(content);
        roommatesList = new ArrayList<>();
        loadingOverlay = view.findViewById(R.id.chore_loading_overlay);

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

    private void setDescriptionEditText(TextView textView, EditText editText) {
        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                textView.setText(s.length() +"/100");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        editText.addTextChangedListener(mTextEditorWatcher);
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

    //buttons initialization
    private void setButtons() {
        editTitleBtn.setOnClickListener(view -> editTitleButtonListener());
        editAssigneeBtn.setOnClickListener(view -> editAssigneeButtonListener());
        editDueDateBtn.setOnClickListener(view -> editDueDateButtonListener());
        editContentBtn.setOnClickListener(view -> editContentButtonListener());
    }

    private void editContentButtonListener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.create();
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_change_content,null);
        Button changeBtn = customLayout.findViewById(R.id.editChoreContentBtn);
        EditText contentEditText = customLayout.findViewById(R.id.editTextChoreDescription);
        TextView contentLeftTextView = customLayout.findViewById(R.id.textViewChoreDescriptionLettersLeft);
        setDescriptionEditText(contentLeftTextView,contentEditText);
        contentEditText.setText(content);
        alertDialog.setView(customLayout);
        changeBtn.setOnClickListener((v) -> {
            content = contentEditText.getText().toString();
            choreFragmentViewModel.setContent(choreId, content, house.getId());
            presetContentTextView.setText(content);
            alertDialog.cancel();

        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void editDueDateButtonListener() {
        new SingleDateAndTimePickerDialog.Builder(getContext())
                .curved()
                .title("Pick a Date")
                .mainColor(ContextCompat.getColor(getContext(), R.color.buttonColor))
                .listener(date -> {
                    String pattern = "dd/MM/yyyy HH:mm";
                    DateFormat df = new SimpleDateFormat(pattern);
                    ChoreFragment.this.dueDate = df.format(date);
                    choreFragmentViewModel.setDueDate(choreId,ChoreFragment.this.dueDate,house.getId());
                    presentDateTextView.setText(ChoreFragment.this.dueDate);
                }).display();
    }

    private void editAssigneeButtonListener() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.create();
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_change_assignee,null);
        Button changeBtn = customLayout.findViewById(R.id.changeAssigneeBtn);
        assigneeSpinner = customLayout.findViewById(R.id.newAssigneeSpinner);
        setAssigneeSpinner(assigneeSpinner);
        alertDialog.setView(customLayout);
        changeBtn.setOnClickListener((v) -> {
            LiveData<newChoreJob> job = choreFragmentViewModel.setAssignee(choreId, ChoreFragment.this.assignee, house.getId());
            job.observe(getViewLifecycleOwner(), newChoreJob -> {
                if(newChoreJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS){
                    presentAssigneeTextView.setText(ChoreFragment.this.assignee);
                    alertDialog.cancel();
                } else if (newChoreJob.getJobStatus() == FirestoreJob.JobStatus.ERROR){
                    Toast.makeText(getContext(), getString(R.string.editAssigneeError), Toast.LENGTH_SHORT).show();
                }
            });


        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();
    }

    private void editTitleButtonListener() {
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
                return;
            } else if (title != null && title.equals(getString(R.string.other))) {
                String diffT = editText.getText().toString();
                if(! diffT.equals("")) {
                    title = diffT;
                }
            }

            LiveData<newChoreJob> job = choreFragmentViewModel.setTitle(choreId, title, house.getId());

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
    }

    @Override
    public boolean onBackPressed() {
        Bundle result = new Bundle();
        passFilterAndSortData(result);
        navController.navigate(R.id.action_choreFragment_to_house_chores_fragment_dest, result);
        return true;
    }
    private void passFilterAndSortData(Bundle result) {
        result.putBoolean("isFiltered", getArguments().getBoolean("isFiltered"));
        result.putString("filterBy", getArguments().getString("filterBy"));
        result.putString("filter", getArguments().getString("filter"));
        result.putBoolean("isSorted", getArguments().getBoolean("isSorted"));
        result.putString("sortBy",getArguments().getString("sortBy"));
    }
}