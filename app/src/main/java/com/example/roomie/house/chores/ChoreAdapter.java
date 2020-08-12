package com.example.roomie.house.chores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.roomie.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ChoreAdapter extends RecyclerView.Adapter<ChoreAdapter.ViewHolder> {

    private List<Chore> _choreList;
    private OnChoreListener _mOnChoreListener;

    public ChoreAdapter(List<Chore> choreList, OnChoreListener onChoreListener) {
        _choreList = choreList;
        _mOnChoreListener = onChoreListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View choreView = inflater.inflate(R.layout.one_chore_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(choreView,_mOnChoreListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chore choreItem = _choreList.get(position);
        TextView titleView = holder.title;
        TextView dueDateView = holder.dueDate;
        TextView assigneeView = holder.assignee;

        titleView.setText(choreItem.get_title());
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        dueDateView.setText(df.format(choreItem.get_dueDate()));
        assigneeView.setText(choreItem.get_assignee());

        if (choreItem.get_assignee().equals("")) {
            holder.locked.setVisibility(View.INVISIBLE);
            holder.unlocked.setVisibility(View.VISIBLE);
        } else {
            holder.locked.setVisibility(View.VISIBLE);
            holder.unlocked.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return _choreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView locked;
        public ImageView unlocked;
        public TextView title;
        public TextView dueDate;
        public TextView assignee;
        OnChoreListener onChoreListener;

        public ViewHolder(View view, OnChoreListener onChoreListener){
            super(view);
            locked = view.findViewById(R.id.lockedView);
            unlocked = view.findViewById(R.id.unlockedView);
            title = view.findViewById(R.id.choreTitleHolderView);
            dueDate = view.findViewById(R.id.choreDueDateHolderView);
            assignee = view.findViewById(R.id.choreAssigneeHolderView);
            this.onChoreListener = onChoreListener;
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onChoreListener.onChoreClick(getAdapterPosition());
        }
    }
    public interface OnChoreListener{
        void onChoreClick(int pos);
    }
}
