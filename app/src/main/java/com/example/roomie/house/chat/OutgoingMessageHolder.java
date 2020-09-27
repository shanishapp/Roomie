package com.example.roomie.house.chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomie.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OutgoingMessageHolder extends RecyclerView.ViewHolder {

    private TextView messageContent;

    private TextView messageTime;


    public OutgoingMessageHolder(@NonNull View itemView) {
        super(itemView);

        messageContent = itemView.findViewById(R.id.chat_bubble_message);
        messageTime = itemView.findViewById(R.id.message_time);
    }

    public void setMessageContent(String messageContent) {
        this.messageContent.setText(messageContent);
    }

    public void setMessageTime(Date time) {
        if (time == null) {
            messageTime.setText("...");
            return;
        }

        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        messageTime.setText(localDateFormat.format(time));
    }

}
