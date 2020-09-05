package com.example.roomie.house.house_settings;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;
import com.squareup.picasso.Picasso;

public class RoomieHolder extends RecyclerView.ViewHolder {

    private ImageView profilePicture;

    private TextView roomieName;

    private TextView roomieEmail;

    private TextView roomieRole;

    public RoomieHolder(@NonNull View itemView) {
        super(itemView);

        profilePicture = itemView.findViewById(R.id.house_settings_roomie_holder_profile_picture);
        roomieName = itemView.findViewById(R.id.house_settings_roomie_holder_name);
        roomieEmail = itemView.findViewById(R.id.house_settings_roomie_holder_email);
        roomieRole = itemView.findViewById(R.id.house_settings_roomie_holder_role);
    }

    public void setRoomieName(String name) {
        roomieName.setText(name);
    }

    public void setRoomieEmail(String email) {
        roomieEmail.setText(email);
    }

    public void setRoomieRole(String role) {
        roomieRole.setText(role);
    }

    public void setProfilePicture(String profilePicture) {
        Picasso.get().load(profilePicture).resize(128, 128).centerCrop().into(this.profilePicture);
    }
}
