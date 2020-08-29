package com.example.roomie.house.user_profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.roomie.R;
import com.example.roomie.util.FormValidator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseEditUserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseEditUserProfileFragment extends Fragment {

    private static final int SELECT_PROFILE_PICTURE_CODE = 1;

    private static final int READ_EXTERNAL_STORAGE_CODE = 2;

    private EditUserProfileViewModel editUserProfileViewModel;

    private NavController navController;

    private ImageView profilePicture;

    private Uri profilePictureUri;

    private ImageButton selectProfilePictureButton;

    private EditText username;

    private Button saveChangesButton;

    private FrameLayout loadingOverlay;


    public HouseEditUserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseEditUserProfileFragment.
     */
    public static HouseEditUserProfileFragment newInstance() {
        return new HouseEditUserProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_edit_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editUserProfileViewModel = new ViewModelProvider(this).get(EditUserProfileViewModel.class);
        navController = NavHostFragment.findNavController(this);

        setUIElements(view);
        setListeners();
        setContent();
    }

    private void setUIElements(View view) {
        profilePicture = view.findViewById(R.id.edit_user_profile_profile_picture);
        selectProfilePictureButton = view.findViewById(R.id.edit_user_profile_select_profile_picture_button);
        username = view.findViewById(R.id.edit_user_profile_username_value);
        saveChangesButton = view.findViewById(R.id.edit_user_profile_save_changes_btn);
        loadingOverlay = view.findViewById(R.id.edit_user_profile_loading_overlay);
    }

    private void setListeners() {
        selectProfilePictureButton.setOnClickListener(this::selectProfilePicture);
        saveChangesButton.setOnClickListener(this::saveChanges);
    }

    private void setContent() {
        toggleLoadingOverlay(true);
        editUserProfileViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            if (username == null) {
                return;
            }

            this.username.setText(username);
        });

        editUserProfileViewModel.getProfilePicture().observe(getViewLifecycleOwner(), profilePicture -> {
            if (profilePicture == null) {
                profilePictureUri = null;
                loadDefaultProfilePicture();
            } else {
                profilePictureUri = profilePicture;
                loadProfilePicture(profilePicture);
            }
        });
    }

    private void sendSelectProfilePictureIntent() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, SELECT_PROFILE_PICTURE_CODE);
    }

    private void selectProfilePicture(View view) {
        // check for storage permission
        boolean hasPermission = ActivityCompat
                .checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (hasPermission) {
            sendSelectProfilePictureIntent();
        } else {
            requestPermissions(
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSelectProfilePictureIntent();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.request_permission_title);
                    alertDialog.setMessage(getString(R.string.request_read_external_storage_msg));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.request_permission_approve),
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PROFILE_PICTURE_CODE && resultCode == Activity.RESULT_OK) {
            Picasso.get().setLoggingEnabled(true);
            profilePictureUri = data.getData();
            Picasso.get().load(profilePictureUri).resize(256, 256).centerCrop().into(profilePicture);
        }
    }

    private void saveChanges(View view) {
        toggleLoadingOverlay(true);
        String username = this.username.getText().toString();
        if (!FormValidator.isValidUsername(username)) {
            toggleLoadingOverlay(false);
            Toast.makeText(getContext(), "Invalid username.", Toast.LENGTH_LONG).show();
            return;
        }

        editUserProfileViewModel.updateUserProfile(username, profilePictureUri).observe(getViewLifecycleOwner(), updateUserProfileJob -> {
            switch (updateUserProfileJob.getJobStatus()) {
                case SUCCESS:
                    toggleLoadingOverlay(false);
                    Toast.makeText(getContext(), "Profile updated successfully.", Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_house_edit_user_profile_fragment_dest_to_house_user_profile_fragment_dest);
                    break;
                case ERROR:
                    toggleLoadingOverlay(false);
                    Toast.makeText(getContext(), "Error occurred, please try again.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
            saveChangesButton.setEnabled(false);
        } else {
            loadingOverlay.setVisibility(View.GONE);
            saveChangesButton.setEnabled(true);
        }
    }

    private void loadDefaultProfilePicture() {
        Picasso.get().load(R.drawable.avatar_1).into(this.profilePicture, new Callback() {
            @Override
            public void onSuccess() {
                toggleLoadingOverlay(false);
            }

            @Override
            public void onError(Exception e) {
                toggleLoadingOverlay(false);
            }
        });
    }

    private void loadProfilePicture(Uri profilePicture) {
        Picasso.get().load(profilePicture)
                .resize(256, 256)
                .centerCrop()
                .into(this.profilePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        toggleLoadingOverlay(false);
                    }

                    @Override
                    public void onError(Exception e) {
                        loadDefaultProfilePicture();
                    }
                });
    }
}