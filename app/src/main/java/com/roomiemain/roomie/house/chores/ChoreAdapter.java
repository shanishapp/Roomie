package com.roomiemain.roomie.house.chores;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.house.chores.chore.Chore;
import com.roomiemain.roomie.repositories.HouseRepository;
import com.roomiemain.roomie.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            if(choreItem.is_choreDone()){
                titleView.setPaintFlags(titleView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                dueDateView.setPaintFlags(dueDateView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                assigneeView.setPaintFlags(assigneeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleView.setPaintFlags(titleView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                dueDateView.setPaintFlags(dueDateView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                assigneeView.setPaintFlags(assigneeView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }

            String assignee = choreItem.get_assignee();
            if (assignee == null || assignee.equals("")) {
                assigneeView.setText(R.string.currently_no_assignee);
                holder1.locked.setVisibility(View.INVISIBLE);
                holder1.unlocked.setVisibility(View.VISIBLE);
            } else {
                assigneeView.setText(assignee);
                setAssigneeImage(holder1.profile,choreItem.get_assignee());
                holder1.locked.setVisibility(View.VISIBLE);
                holder1.unlocked.setVisibility(View.INVISIBLE);
            }

            long daysLeft = getDifferenceDays(new Date(),choreItem.get_dueDate());
            holder1.daysLeft.setText(String.valueOf((int)daysLeft));
            if(daysLeft > 99 || daysLeft< -9){
                holder1.hoursLeft.setText("-");
                holder1.daysLeft.setText("-");
            } else {
                if (daysLeft > 3 || daysLeft < 0) {
                    holder1.hoursLeft.setText("-");
                } else {
                    long hoursLeft = getDifferenceHours(new Date(), choreItem.get_dueDate());
                    holder1.hoursLeft.setText(String.valueOf((int) hoursLeft));
                }
            }

        } else {
            MenuViewHolder holder1 = (MenuViewHolder) holder;
            holder1.editLayout.setOnClickListener(view -> _mOnChoreListener.onEditClick(position));
            holder1.deleteLayout.setOnClickListener(view -> _mOnChoreListener.onDeleteClick(position));
            TextView textView = holder1.markAsDoneLayout.findViewById(R.id.markAsDoneTextView);
            LottieAnimationView animationView = holder1.markAsDoneLayout.findViewById(R.id.lottieDoneAnimation);
            if(choreItem.is_choreDone()) {
                textView.setText(R.string.markAsUnDone);
                animationView.setAnimation("28045-dislike.json");

            } else {
                textView.setText(R.string.markAsDone);
                animationView.setAnimation("28044-like.json");
            }

            holder1.markAsDoneLayout.setOnClickListener(view -> _mOnChoreListener.onMarkAsDoneClick(position));
        }
    }

    private static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private static long getDifferenceHours(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private void setAssigneeImage(ImageView profile, String assignee) {
        if(_rommiesNameToImages.containsKey(assignee) && _rommiesNameToImages.get(assignee) != null && !_rommiesNameToImages.get(assignee).equals("")) {
            Picasso.get().setLoggingEnabled(true);
            Uri profilePictureUri = Uri.parse(_rommiesNameToImages.get(assignee));
            Picasso.get().load(profilePictureUri).resize(60, 60).centerCrop().into(profile);
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
        public TextView daysLeft;
        public TextView hoursLeft;
        OnChoreListener onChoreListener;

        public ViewHolder(View view, OnChoreListener onChoreListener){
            super(view);
            locked = view.findViewById(R.id.lockedView);
            unlocked = view.findViewById(R.id.unlockedView);
            title = view.findViewById(R.id.choreTitleHolderView);
            dueDate = view.findViewById(R.id.choreDueDateHolderView);
            assignee = view.findViewById(R.id.choreAssigneeHolderView);
            profile = view.findViewById(R.id.assigneeImageView);
            daysLeft = view.findViewById(R.id.daysLeftTextView);
            hoursLeft = view.findViewById(R.id.hoursLeftTextView);
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
        notifyDataSetChanged();
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
