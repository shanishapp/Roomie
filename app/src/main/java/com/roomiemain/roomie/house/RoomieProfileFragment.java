package com.roomiemain.roomie.house;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roomiemain.roomie.R;
import com.roomiemain.roomie.User;
import com.roomiemain.roomie.house.user_profile.DataListAdapter;
import com.roomiemain.roomie.repositories.GetChoresJob;
import com.roomiemain.roomie.repositories.GetExpensesJob;
import com.roomiemain.roomie.repositories.GetUserJob;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomieProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomieProfileFragment extends Fragment {

    private HouseActivityViewModel houseActivityViewModel;

    private RoomieProfileViewModel roomieProfileViewModel;

    private TextView roomieName;

    private TextView roomieEmail;

    private ImageView roomieProfilePicture;

    private TextView roomieHouse;

    private LinearLayout roomieBroomsWrapper;

    private TextView roomieBrooms;

    private LinearLayout roomieChoresWrapper;

    private TextView roomieChores;

    private LinearLayout roomieExpensesWrapper;

    private TextView roomieExpenses;

    private DataListDialogFragment doneChoresDialogFragment;

    private DataListDialogFragment choresDialogFragment;

    private DataListDialogFragment expensesDialogFragment;

    private FrameLayout loadingOverlay;

    private AtomicInteger overlayCounter;

    private static final int OVERLAY_COUNTER_TARGET = 4;


    // TODO merge user profile and roomie profile to one fragment, they are practically the same
    public RoomieProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RoomieProfileFragment.
     */
    public static RoomieProfileFragment newInstance() {
        return new RoomieProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roomie_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        overlayCounter = new AtomicInteger(0);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        roomieProfileViewModel = new ViewModelProvider(this).get(RoomieProfileViewModel.class);

        doneChoresDialogFragment = DataListDialogFragment.newInstance(this, DataListDialogFragment.DIALOG_DONE_CHORES);
        choresDialogFragment = DataListDialogFragment.newInstance(this, DataListDialogFragment.DIALOG_CHORES);
        expensesDialogFragment = DataListDialogFragment.newInstance(this, DataListDialogFragment.DIALOG_EXPENSES);

        setUIElements(view);
        setListeners();
        setContent();
    }

    private void setUIElements(View view) {
        roomieName = view.findViewById(R.id.roomie_profile_name);
        roomieEmail = view.findViewById(R.id.roomie_profile_email);
        roomieProfilePicture = view.findViewById(R.id.roomie_profile_profile_picture);
        roomieHouse = view.findViewById(R.id.roomie_profile_house_text);
        roomieBroomsWrapper = view.findViewById(R.id.house_roomie_profile_brooms_wrapper);
        roomieBrooms = view.findViewById(R.id.roomie_profile_brooms_value);
        roomieChoresWrapper = view.findViewById(R.id.house_roomie_profile_chores_wrapper);
        roomieChores = view.findViewById(R.id.roomie_profile_chores_value);
        roomieExpensesWrapper = view.findViewById(R.id.house_roomie_profile_expenses_wrapper);
        roomieExpenses = view.findViewById(R.id.roomie_profile_expenses_value);
        loadingOverlay = view.findViewById(R.id.roomie_profile_loading_overlay);
    }

    private void setListeners() {
        roomieBroomsWrapper.setOnClickListener( view -> doneChoresDialogFragment.showDialog() );
        roomieChoresWrapper.setOnClickListener( view -> choresDialogFragment.showDialog() );
        roomieExpensesWrapper.setOnClickListener( view -> expensesDialogFragment.showDialog() );
    }

    private void setContent() {
        String userId = getArguments().getString("userId");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid user id.", Toast.LENGTH_LONG).show();
            return;
        }

        toggleLoadingOverlay(true);
        LiveData<GetUserJob> job = roomieProfileViewModel.getRoomie(userId);
        job.observe(getViewLifecycleOwner(), getUserJob -> {
            switch (getUserJob.getJobStatus()) {
                case SUCCESS:
                    User user = getUserJob.getUser();

                    roomieProfileViewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
                        roomieName.setText(name);
                    });
                    roomieProfileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
                        roomieEmail.setText(email);
                    });

                    roomieProfileViewModel.getUserProfilePicture().observe(getViewLifecycleOwner(), profilePicture -> {
                        if (profilePicture == null || profilePicture.isEmpty()) {
                            overlayCounterIncCheck();
                            return;
                        }

                        loadProfilePicture(profilePicture);
                    });

                    BidiFormatter bidiFormatter = BidiFormatter.getInstance();
                    String houseName = houseActivityViewModel.getHouse().getName();
                    roomieHouse.setText(String.format(
                            getString(R.string.roomie_profile_fragment_roomie_in), bidiFormatter.unicodeWrap(houseName)
                    ));

                    roomieProfileViewModel.getUserBrooms().observe(getViewLifecycleOwner(), brooms -> {
                        roomieBrooms.setText(String.valueOf(brooms));
                    });
                    roomieProfileViewModel.getUserChores().observe(getViewLifecycleOwner(), chores -> {
                        roomieChores.setText(String.valueOf(chores));
                    });
                    roomieProfileViewModel.getUserExpenses().observe(getViewLifecycleOwner(), expenses -> {
                        roomieExpenses.setText(expenses + "\u20aa");
                    });


                    roomieProfileViewModel.setRoomieProfilePicture(user.getProfilePicture());
                    // load content from db
                    LiveData<GetChoresJob> doneChoresJob = roomieProfileViewModel
                            .loadDoneChores(houseActivityViewModel.getHouse().getId(), user.getUsername());
                    LiveData<GetChoresJob> undoneChoresJob = roomieProfileViewModel
                            .loadUndoneChores(houseActivityViewModel.getHouse().getId(), user.getUsername());
                    LiveData<GetExpensesJob> expensesJob = roomieProfileViewModel
                            .loadExpenses(houseActivityViewModel.getHouse().getId(), user.getUid());

                    doneChoresJob.observe(getViewLifecycleOwner(), job1 -> {
                        switch (job1.getJobStatus()) {
                            case SUCCESS:
                                roomieProfileViewModel.setDoneChoreList(job1.getChoreList());
                                overlayCounterIncCheck();
                                break;
                            case ERROR:
                                Toast.makeText(getContext(), "Error loading done chores.", Toast.LENGTH_LONG).show();
                                overlayCounterIncCheck();
                                break;
                            default:
                                break;
                        }
                    });

                    undoneChoresJob.observe(getViewLifecycleOwner(), job2 -> {
                        switch (job2.getJobStatus()) {
                            case SUCCESS:
                                roomieProfileViewModel.setUndoneChoreList(job2.getChoreList());
                                overlayCounterIncCheck();
                                break;
                            case ERROR:
                                Toast.makeText(getContext(), "Error loading undone chores.", Toast.LENGTH_LONG).show();
                                overlayCounterIncCheck();
                                break;
                            default:
                                break;
                        }
                    });

                    expensesJob.observe(getViewLifecycleOwner(), job3 -> {
                        switch (job3.getJobStatus()) {
                            case SUCCESS:
                                roomieProfileViewModel.setExpenseList(job3.getExpenseList());
                                overlayCounterIncCheck();
                                break;
                            case ERROR:
                                Toast.makeText(getContext(), "Error loading expenses.", Toast.LENGTH_LONG).show();
                                overlayCounterIncCheck();
                                break;
                            default:
                                break;
                        }
                    });
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Error fetching roomie data.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });

    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
        } else {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void loadProfilePicture(String profilePicture) {
        Picasso.get().load(profilePicture)
                .resize(256, 256)
                .centerCrop()
                .into(this.roomieProfilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        overlayCounterIncCheck();
                    }

                    @Override
                    public void onError(Exception e) {
                        overlayCounterIncCheck();
                    }
                });
    }

    private void overlayCounterIncCheck() {
        overlayCounter.getAndIncrement();
        if (overlayCounter.get() == OVERLAY_COUNTER_TARGET) {
            toggleLoadingOverlay(false);
        }
    }

    public static class DataListDialogFragment extends DialogFragment {

        public static final int DIALOG_DONE_CHORES = 1;

        public static final int DIALOG_CHORES = 2;

        public static final int DIALOG_EXPENSES = 3;


        private int dialogType;

        private RecyclerView recyclerView;

        private RecyclerView.LayoutManager layoutManager;

        private DataListAdapter dataListAdapter;

        private RoomieProfileFragment parent;

        private TextView dialogTitle;


        public static DataListDialogFragment newInstance(RoomieProfileFragment parent, int dialogType) {
            DataListDialogFragment dataListDialogFragment = new DataListDialogFragment();
            dataListDialogFragment.setParent(parent);
            dataListDialogFragment.setDialogType(dialogType);

            return dataListDialogFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.profile_datalist_dialog, container, false);

            recyclerView = v.findViewById(R.id.profile_datalist_recycler_view);
            layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            dialogTitle = v.findViewById(R.id.profile_dialog_title);
            if (dialogType == DIALOG_DONE_CHORES) {
                dialogTitle.setText(getString(R.string.house_user_profile_fragment_done_chores_dialog_title));
                dataListAdapter = new DataListAdapter(getContext(), DataListAdapter.CHORE_TYPE);
            } else if (dialogType == DIALOG_CHORES) {
                dialogTitle.setText(getString(R.string.house_user_profile_fragment_chores_dialog_title));
                dataListAdapter = new DataListAdapter(getContext(), DataListAdapter.CHORE_TYPE);
            } else {
                dialogTitle.setText(getString(R.string.house_user_profile_fragment_expenses_dialog_title));
                dataListAdapter = new DataListAdapter(getContext(), DataListAdapter.EXPENSE_TYPE);
            }
            recyclerView.setAdapter(dataListAdapter);
            setContent();

            return v;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            return dialog;
        }

        private void showDialog() {
            FragmentManager fragmentManager = parent.getParentFragmentManager();
            this.show(fragmentManager, "DataList Dialog");
        }

        private void setContent() {
            if (dialogType == DIALOG_DONE_CHORES) {
                dataListAdapter.setChoreList(parent.roomieProfileViewModel.getDoneChoreList());
            } else if (dialogType == DIALOG_CHORES) {
                dataListAdapter.setChoreList(parent.roomieProfileViewModel.getUndoneChoreList());
            } else if (dialogType == DIALOG_EXPENSES) {
                dataListAdapter.setExpenseList(parent.roomieProfileViewModel.getExpenseList());
            }
        }

        private void setParent(RoomieProfileFragment parent) {
            this.parent = parent;
        }

        private void setDialogType(int dialogType) {
            this.dialogType = dialogType;
        }

    }
}