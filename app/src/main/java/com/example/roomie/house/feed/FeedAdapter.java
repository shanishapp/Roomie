package com.example.roomie.house.feed;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;
import com.example.roomie.house.chores.ChoreAdapter;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedAdapter extends RecyclerView .Adapter<RecyclerView.ViewHolder> {

    private final OnFeedListener onFeedListener;
    private List<Feed> feedList;
    private HashMap<String,String> _rommiesNameToImages;

    public FeedAdapter(List<Feed> feedList, OnFeedListener onFeedListener, ArrayList<String> roommiesNames, ArrayList<String> rommiesProfiles){
        this.feedList = feedList;
        this.onFeedListener = onFeedListener;
        _rommiesNameToImages = new HashMap<>();
        for(int i = 0; i < roommiesNames.size(); i++){
            _rommiesNameToImages.put(roommiesNames.get(i),rommiesProfiles.get(i));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return feedList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.feed_item, parent, false);
        return new FeedViewHolder(view,onFeedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Feed feed = feedList.get(position);
        FeedViewHolder feedViewHolder = (FeedViewHolder) holder;
//        Picasso.get().load(feed.getRoommie().getProfilePicture()).into(feedViewHolder.feedCreatorImageView);
        feedViewHolder.feedDescription.setText(feed.getDescription());
        feedViewHolder.feedRoommateName.setText(feed.getRoommieName());
        feedViewHolder.feedDateCreated.setText(feed.getDateCreated().toString());

        if(feed.getRoommieName()!= null && !feed.getRoommieName().equals("")) {
            Picasso.get().setLoggingEnabled(true);
            Uri profilePictureUri = Uri.parse(_rommiesNameToImages.get(feed.getRoommieName()));
            Picasso.get().load(profilePictureUri).resize(60, 60).centerCrop().into(feedViewHolder.feedCreatorImageView);
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public interface OnFeedListener{
        void onFeedClicked(int pos);
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView feedRoommateName;
        public TextView feedDescription;
        public TextView feedDateCreated;
        public ImageView feedCreatorImageView;
        public OnFeedListener onFeedListener;

        public FeedViewHolder(View view, OnFeedListener onFeedListener){
            super(view);
            feedRoommateName = view.findViewById(R.id.feedRoommateName);
            feedDescription = view.findViewById(R.id.feedDescription);
            feedDateCreated = view.findViewById(R.id.feedDateCreated);
            feedCreatorImageView = view.findViewById(R.id.feed_creator_image_view);
            this.onFeedListener = onFeedListener;
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onFeedListener.onFeedClicked(getAdapterPosition());
        }
    }
}
