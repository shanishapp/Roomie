package com.example.roomie.house.house_settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;
import com.example.roomie.User;

import java.util.List;

public class RoomieAdapter extends RecyclerView.Adapter<RoomieHolder> {

    private LayoutInflater inflater;

    private List<User> roomiesList;


    public RoomieAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RoomieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.house_settings_roomie_holder, parent, false);
        final RoomieHolder roomieHolder = new RoomieHolder(view);

        return roomieHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomieHolder holder, int position) {
        if (roomiesList == null) {
            return;
        }

        User roomie = roomiesList.get(position);
        holder.setProfilePicture(roomie.getProfilePicture());
        holder.setRoomieName(roomie.getUsername());
        holder.setRoomieRole(roomie.getRole());
    }

    @Override
    public int getItemCount() {
        if (roomiesList == null) {
            return 0;
        }

        return roomiesList.size();
    }

    public void setRoomiesList(List<User> roomiesList) {
        this.roomiesList = roomiesList;
        notifyDataSetChanged();
    }

}
