package com.example.roomie.house.chores;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.roomie.R;
import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.repositories.HouseRepository;
import com.example.roomie.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SHOW_MENU = 1;
    private static final int HIDE_MENU = 2;

    private List<Chore> _choreList;
    private OnChoreListener _mOnChoreListener;
    private HashMap<String,String> _rommiesNameToImages;

    public ChoreAdapter(List<Chore> choreList, OnChoreListener onChoreListener, ArrayList<String> roommiesNames, ArrayList<String> rommiesProfiles) {
        _choreList = choreList;
        _mOnChoreListener = onChoreListener;
        _rommiesNameToImages = new HashMap<>();
        for(int i = 0; i < roommiesNames.size(); i++){
            _rommiesNameToImages.put(roommiesNames.get(i),rommiesProfiles.get(i));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(_choreList.get(position).isShowMenu()){
            return SHOW_MENU;
        }else{
            return HIDE_MENU;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View choreView;
        // Inflate the custom layout
        if(viewType == SHOW_MENU) {
            choreView = inflater.inflate(R.layout.one_chore_menu, parent, false);
            return new MenuViewHolder(choreView);
        } else {
            choreView = inflater.inflate(R.layout.one_chore_item, parent, false);
            return new ChoreAdapter.ViewHolder(choreView,_mOnChoreListener);
        }
        // Return a new holder instance
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chore choreItem = _choreList.get(position);
        if(holder instanceof ChoreAdapter.ViewHolder) {
            ChoreAdapter.ViewHolder holder1 = (ChoreAdapter.ViewHolder) holder;
            TextView titleView = holder1.title;
            TextView dueDateView = holder1.dueDate;
            TextView assigneeView = holder1.assignee;
            ImageView profileImage = holder1.profile;
            titleView.setText(choreItem.get_title());
            String pattern = "dd/MM/yyyy HH:mm";
            DateFormat df = new SimpleDateFormat(pattern);
            dueDateView.setText(df.format(choreItem.get_dueDate()));
            assigneeView.setText(choreItem.get_assignee());
            if(choreItem.is_choreDone()){
                titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                dueDateView.setPaintFlags(dueDateView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                assigneeView.setPaintFlags(assigneeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleView.setPaintFlags(titleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                dueDateView.setPaintFlags(dueDateView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                assigneeView.setPaintFlags(assigneeView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }


            if (choreItem.get_assignee() == null) {
                holder1.locked.setVisibility(View.INVISIBLE);
                holder1.unlocked.setVisibility(View.VISIBLE);
            } else {
                setAssigneeImage(holder1.profile,choreItem.get_assignee());
                holder1.locked.setVisibility(View.VISIBLE);
                holder1.unlocked.setVisibility(View.INVISIBLE);
            }
        } else {
            MenuViewHolder holder1 = (MenuViewHolder) holder;
            holder1.editLayout.setOnClickListener(view -> _mOnChoreListener.onEditClick(position));
            holder1.deleteLayout.setOnClickListener(view -> _mOnChoreListener.onDeleteClick(position));
            holder1.markAsDoneLayout.setOnClickListener(view -> _mOnChoreListener.onMarkAsDoneClick(position));
        }
    }

    private void setAssigneeImage(ImageView profile, String assignee) {
        if(_rommiesNameToImages.containsKey(assignee) && _rommiesNameToImages.get(assignee) != null && !_rommiesNameToImages.get(assignee).equals("")) {
            Picasso.get().load(_rommiesNameToImages.get(assignee)).into(profile);
        }
    }

    @Override
    public int getItemCount() {
        return _choreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView locked;
        public ImageView unlocked;
        public ImageView profile;
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
            profile = view.findViewById(R.id.assigneeImageView);
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
        void onDeleteClick(int pos);
        void onEditClick(int pos);
        void onMarkAsDoneClick(int pos);
    }

    //Our menu view
    public class MenuViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout deleteLayout;
        public RelativeLayout editLayout;
        public RelativeLayout markAsDoneLayout;

        public MenuViewHolder(View view){
            super(view);
            deleteLayout =view.findViewById(R.id.deleteLayoutButton);
            editLayout = view.findViewById(R.id.editLayoutButton);
            markAsDoneLayout = view.findViewById(R.id.markAsDoneLayoutButton);
        }
    }

    public void showMenu(int position) {
        for(int i=0; i<_choreList.size(); i++){
            _choreList.get(i).setShowMenu(false);
        }
        _choreList.get(position).setShowMenu(true);
        notifyDataSetChanged();// TODO is this working ?
    }

    public boolean isMenuShown() {
        for(int i=0; i<_choreList.size(); i++){
            if(_choreList.get(i).isShowMenu()){
                return true;
            }
        }
        return false;
    }

    public void closeMenu() {
        for(int i=0; i<_choreList.size(); i++){
            _choreList.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }
}
