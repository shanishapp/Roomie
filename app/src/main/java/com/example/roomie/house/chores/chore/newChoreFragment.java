package com.example.roomie.house.chores.chore;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.roomie.House;
import com.example.roomie.R;
import com.example.roomie.User;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ImageButton setDateImageView;
    private TextView presentDateTextView;
    private HouseActivityViewModel houseActivityViewModel;
    private House house;
    private Button createChoreButton;
    private NavController navController ;
    private ArrayList<String> roommatesList;
    private Date dueDate = null;
    private Date snoozeDate = null;
    private String title = null;
    private String assignee = null;
    private FrameLayout loadingOverlay;
    private LottieAnimationView remindMe;


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
        //set up add button

        createNotificationChannel();
        initViews(view);
        toggleLoadingOverlay(true);
        loadRoommies(view);

    }

    private void initViews(View view) {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();

        createChoreButton = view.findViewById(R.id.createChoreBtn);
        navController = Navigation.findNavController(view);
        differentTitleEditText = view.findViewById(R.id.differentTitleEditText);
        titleSpinner = view.findViewById(R.id.titleSpinner) ;
        roommatesList = new ArrayList<>();
        setTitleSpinner();
        assigneeSpinner = view.findViewById(R.id.assigneeSpinner);
        setDateImageView = view.findViewById(R.id.setDueDateButton);
        presentDateTextView = view.findViewById(R.id.presentDateTextView);
        remindMe = view.findViewById(R.id.reminder_animation);
        dueDate = new Date();
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        presentDateTextView.setText(df.format(dueDate));
        initAnimationListener();
        loadingOverlay = view.findViewById(R.id.new_chore_loading_overlay);
        createChoreButton.setOnClickListener(view1 -> {
            if (view1 != null) {
                doCreateNewChore(view);
            }
        });
    }

    private void initAnimationListener() {
       setDateImageView.setOnClickListener(view -> new SingleDateAndTimePickerDialog.Builder(getContext())
               .bottomSheet()
               .curved()
               .title("Pick a Date")
               .listener(date -> {
                   newChoreFragment.this.dueDate = date;
                   String pattern = "dd/MM/yyyy HH:mm";
                   DateFormat df = new SimpleDateFormat(pattern);
                   presentDateTextView.setText(df.format(date));
               }).display());
       remindMe.setOnClickListener(view -> new SingleDateAndTimePickerDialog.Builder(getContext())
               .bottomSheet()
               .curved()
               .title("Pick a Date")
               .listener(date -> {
                   //TODO save the snooze date in firestore
                   snoozeDate = date;
               }).display());
    }

    private void setAssigneeSpinner() {
        assigneeSpinner.setItems(roommatesList);
        assigneeSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            assignee = s;
        });
        assigneeSpinner.setSpinnerOutsideTouchListener((view, motionEvent) -> assigneeSpinner.dismiss());
        assigneeSpinner.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void loadRoommies(View view) {
        LiveData<GetHouseRoomiesJob> job = HouseRepository.getInstance().getHouseRoomies(house.getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            switch (getHouseRoomiesJob.getJobStatus()){
                case SUCCESS:
                    for (User user: getHouseRoomiesJob.getRoomiesList()){
                        roommatesList.add(user.getUsername());
                    }
                    createChoreButton.setEnabled(true);
                    toggleLoadingOverlay(false);
                    setAssigneeSpinner();
            }
        });
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



        LiveData<newChoreJob> job = newChoreFragmentViewModel.createNewChore(house,title, dueDate,assignee,snoozeDate);

        job.observe(getViewLifecycleOwner(), createNewChoreJob -> {
            switch (createNewChoreJob.getJobStatus()) {
                case IN_PROGRESS:
                    createChoreButton.setEnabled(false);
                    break;
                case SUCCESS:
                    if(snoozeDate != null)
                        setSnooze(createNewChoreJob);
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

    private void setSnooze(newChoreJob createNewChoreJob) {
        Intent intent = new Intent(getContext(), SnoozerBroadcast.class);
        intent.putExtra("title",createNewChoreJob.getChore().get_title());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                snoozeDate.getTime(),
                pendingIntent);
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            createChoreButton.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            createChoreButton.setEnabled(true);
        }
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notifyRoommie";
            String description = "to be added later";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyRoommie",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}