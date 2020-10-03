package com.example.roomie.house.user_profile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetChoresJob;
import com.example.roomie.repositories.GetExpensesJob;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseUserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseUserProfileFragment extends Fragment {

    private NavController navController;

    private HouseActivityViewModel houseActivityViewModel;

    private UserProfileViewModel userProfileViewModel;

    private TextView userName;

    private TextView userEmail;

    private ImageView userProfilePicture;

    private TextView userHouse;

    private LinearLayout userBroomsWrapper;

    private TextView userBrooms;

    private LinearLayout userChoresWrapper;

    private TextView userChores;

    private LinearLayout userExpensesWrapper;

    private TextView userExpenses;

    private Button editProfileButton;

    private DataListDialogFragment doneChoresDialogFragment;

    private DataListDialogFragment choresDialogFragment;

    private DataListDialogFragment expensesDialogFragment;

    private FrameLayout loadingOverlay;

    private AtomicInteger overlayCounter;

    private static final int OVERLAY_COUNTER_TARGET = 4;


    public HouseUserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseUserProfileFragment.
     */
    public static HouseUserProfileFragment newInstance() {
        return new HouseUserProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        overlayCounter = new AtomicInteger(0);
        navController = NavHostFragment.findNavController(this);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        doneChoresDialogFragment = DataListDialogFragment.newInstance(this, DataListDialogFragment.DIALOG_DONE_CHORES);
        choresDialogFragment = DataListDialogFragment.newInstance(this, DataListDialogFragment.DIALOG_CHORES);
        expensesDialogFragment = DataListDialogFragment.newInstance(this, DataListDialogFragment.DIALOG_EXPENSES);

        setUIElements(view);
        setListeners();
        setContent();
    }

    private void setUIElements(View view) {
        userName = view.findViewById(R.id.user_profile_name);
        userEmail = view.findViewById(R.id.user_profile_email);
        userProfilePicture = view.findViewById(R.id.user_profile_profile_picture);
        userHouse = view.findViewById(R.id.user_profile_house_text);
        userBroomsWrapper = view.findViewById(R.id.house_user_profile_brooms_wrapper);
        userBrooms = view.findViewById(R.id.user_profile_brooms_value);
        userChoresWrapper = view.findViewById(R.id.house_user_profile_chores_wrapper);
        userChores = view.findViewById(R.id.user_profile_chores_value);
        userExpensesWrapper = view.findViewById(R.id.house_user_profile_expenses_wrapper);
        userExpenses = view.findViewById(R.id.user_profile_expenses_value);
        editProfileButton = view.findViewById(R.id.user_profile_edit_profile_button);
        loadingOverlay = view.findViewById(R.id.user_profile_loading_overlay);
    }

    private void setListeners() {
        editProfileButton.setOnClickListener(this::gotoEditProfile);
        userBroomsWrapper.setOnClickListener( view -> doneChoresDialogFragment.showDialog() );
        userChoresWrapper.setOnClickListener( view -> choresDialogFragment.showDialog() );
        userExpensesWrapper.setOnClickListener( view -> expensesDialogFragment.showDialog() );
    }

    private void setContent() {
        toggleLoadingOverlay(true);

        userProfileViewModel.getUserName().observe(getViewLifecycleOwner(), username -> {
            userName.setText(username);
        });
        userProfileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            userEmail.setText(email);
        });
        userProfileViewModel.getUserProfilePicture().observe(getViewLifecycleOwner(), profilePicture -> {
            if (profilePicture == null || profilePicture.toString().isEmpty()) {
                overlayCounterIncCheck();
                return;
            }

            loadProfilePicture(profilePicture);
        });

        BidiFormatter bidiFormatter = BidiFormatter.getInstance();
        String houseName = houseActivityViewModel.getHouse().getName();
        userHouse.setText(String.format(
                getString(R.string.house_user_profile_fragment_roomie_in), bidiFormatter.unicodeWrap(houseName)
        ));

        userProfileViewModel.getUserBrooms().observe(getViewLifecycleOwner(), brooms -> {
            userBrooms.setText(String.valueOf(brooms));
        });
        userProfileViewModel.getUserChores().observe(getViewLifecycleOwner(), chores -> {
            userChores.setText(String.valueOf(chores));
        });
        userProfileViewModel.getUserExpenses().observe(getViewLifecycleOwner(), expenses -> {
            userExpenses.setText(expenses + "\u20aa");
        });

        // load content from db
        LiveData<GetChoresJob> doneChoresJob = userProfileViewModel.loadDoneChores(houseActivityViewModel.getHouse().getId());
        LiveData<GetChoresJob> undoneChoresJob = userProfileViewModel.loadUndoneChores(houseActivityViewModel.getHouse().getId());
        LiveData<GetExpensesJob> expensesJob = userProfileViewModel.loadExpenses(houseActivityViewModel.getHouse().getId());
        doneChoresJob.observe(getViewLifecycleOwner(), job -> {
            switch (job.getJobStatus()) {
                case SUCCESS:
                    userProfileViewModel.setDoneChoreList(job.getChoreList());
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

        undoneChoresJob.observe(getViewLifecycleOwner(), job1 -> {
            switch (job1.getJobStatus()) {
                case SUCCESS:
                    userProfileViewModel.setUndoneChoreList(job1.getChoreList());
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

        expensesJob.observe(getViewLifecycleOwner(), job2 -> {
            switch (job2.getJobStatus()) {
                case SUCCESS:
                    userProfileViewModel.setExpenseList(job2.getExpenseList());
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
    }

    private void gotoEditProfile(View view) {
        navController.navigate(R.id.action_house_user_profile_fragmnet_dest_to_house_edit_user_profile_fragment);
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            editProfileButton.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            editProfileButton.setEnabled(true);
        }
    }

    private void loadProfilePicture(Uri profilePicture) {
        Picasso.get().load(profilePicture)
                .resize(256, 256)
                .centerCrop()
                .into(this.userProfilePicture, new Callback() {
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

        private HouseUserProfileFragment parent;

        private TextView dialogTitle;


        public static DataListDialogFragment newInstance(HouseUserProfileFragment parent, int dialogType) {
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
                dataListAdapter.setChoreList(parent.userProfileViewModel.getDoneChoreList());
            } else if (dialogType == DIALOG_CHORES) {
                dataListAdapter.setChoreList(parent.userProfileViewModel.getUndoneChoreList());
            } else if (dialogType == DIALOG_EXPENSES) {
                dataListAdapter.setExpenseList(parent.userProfileViewModel.getExpenseList());
            }
        }

        private void setParent(HouseUserProfileFragment parent) {
            this.parent = parent;
        }

        private void setDialogType(int dialogType) {
            this.dialogType = dialogType;
        }

    }
}