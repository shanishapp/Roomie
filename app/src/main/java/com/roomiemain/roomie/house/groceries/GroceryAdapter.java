package com.roomiemain.roomie.house.groceries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomiemain.roomie.R;
import com.roomiemain.roomie.house.groceries.grocery.Grocery;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class GroceryAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_GROUP = 0;
    private static final int VIEWTYPE_GROCERY = 1;
    private List<Grocery> _groceryList;
    private OnGroceryListener _onGroceryListener;

    public GroceryAdapter(List<Grocery> groceryList, OnGroceryListener onGroceryListener) {
        _groceryList = groceryList;
        _onGroceryListener = onGroceryListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
//        View v = inflater.inflate(R.layout.one_grocery_item, parent, false);
        if(viewType == VIEWTYPE_GROUP){
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.group_item,parent,false);
            GroupViewHolder groupViewHolder = new GroupViewHolder(group);
            return groupViewHolder;
        } else if(viewType == VIEWTYPE_GROCERY) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.one_grocery_item,parent,false);
            GroceryViewHolder groceryViewHolder = new GroceryViewHolder(group,_onGroceryListener);
            return groceryViewHolder;
        }
        ViewGroup group = (ViewGroup) inflater.inflate(R.layout.group_item,parent,false);
        GroupViewHolder groupViewHolder = new GroupViewHolder(group);
        return groupViewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return _groceryList.get(position).get_viewType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof GroupViewHolder){
            GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
            groupViewHolder.groupTitleTextView.setText(_groceryList.get(position).get_name());
        } else if(holder instanceof GroceryViewHolder){
            Grocery groceryItem = _groceryList.get(position);
            GroceryViewHolder groceryViewHolder = (GroceryViewHolder) holder;
            groceryViewHolder.presetGroceryTextView.setText(groceryItem.get_name());
            String pattern = "dd/MM/yyyy HH:mm";
            DateFormat df = new SimpleDateFormat(pattern);
            groceryViewHolder.pickGroceryCheckBox.setCheckedColor(R.color.colorAccent);
            groceryViewHolder.pickGroceryCheckBox.setChecked(false);
            groceryViewHolder.pickGroceryCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
                if(isChecked){
                    _onGroceryListener.onGroceryPicked(groceryItem);
                } else {
                    _onGroceryListener.onGroceryUnPicked(groceryItem);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return _groceryList.size();
    }

    public interface OnGroceryListener{
        boolean onGroceryClick(int pos, CustomCheckBox pickGroceryCheckBox);
        void onGroceryPicked(Grocery grocery);
        void onGroceryUnPicked(Grocery grocery);
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CustomCheckBox pickGroceryCheckBox;
        public TextView presetGroceryTextView;
        OnGroceryListener onGroceryListener;

        public GroceryViewHolder(View view, OnGroceryListener onGroceryListener){
            super(view);
            pickGroceryCheckBox = view.findViewById(R.id.pickGroceryCheckBox);
            presetGroceryTextView = view.findViewById(R.id.presentGroceryTextView);
            this.onGroceryListener = onGroceryListener;
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onGroceryListener.onGroceryClick(getAdapterPosition(),pickGroceryCheckBox);
        }
    }

    private class GroupViewHolder extends RecyclerView.ViewHolder{

        TextView groupTitleTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupTitleTextView = (TextView) itemView.findViewById(R.id.date_group_title);
        }
    }
}
