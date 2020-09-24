package com.example.roomie.house.groceries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;
import com.example.roomie.house.groceries.grocery.Grocery;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class GroceryAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        View v = inflater.inflate(R.layout.one_grocery_item, parent, false);
        return new GroceryAdapter.ViewHolder(v,_onGroceryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Grocery groceryItem = _groceryList.get(position);
        GroceryAdapter.ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.presetGroceryTextView.setText(groceryItem.get_name());
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        ((ViewHolder) holder).creationDateTextView.setText(df.format(groceryItem.get_creationDate()));
        viewHolder.pickGroceryCheckBox.setCheckedColor(R.color.colorAccent);
        viewHolder.pickGroceryCheckBox.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if(isChecked){
                _onGroceryListener.onGroceryPicked(groceryItem);
            } else {
                _onGroceryListener.onGroceryUnPicked(groceryItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _groceryList.size();
    }

    public interface OnGroceryListener{
        boolean onGroceryLongClick(int pos);
        void onGroceryPicked(Grocery grocery);
        void onGroceryUnPicked(Grocery grocery);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public CustomCheckBox pickGroceryCheckBox;
        public TextView presetGroceryTextView;
        public TextView creationDateTextView;
        OnGroceryListener onGroceryListener;

        public ViewHolder(View view, OnGroceryListener onGroceryListener){
            super(view);
            pickGroceryCheckBox = view.findViewById(R.id.pickGroceryCheckBox);
            presetGroceryTextView = view.findViewById(R.id.presentGroceryTextView);
            creationDateTextView = view.findViewById(R.id.creationDateTextView);
            this.onGroceryListener = onGroceryListener;
            view.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View view) {
            return onGroceryListener.onGroceryLongClick(getAdapterPosition());
        }
    }

}
