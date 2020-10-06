package com.roomiemain.roomie.house.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roomiemain.roomie.R;
import com.roomiemain.roomie.User;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;

    private List<User> roomiesList;

    private List<MessageUi> messageList;


    public MessageAdapter(Context context, List<User> roomiesList) {
        inflater = LayoutInflater.from(context);
        this.roomiesList = roomiesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MessageUi.OUTGOING_MESSAGE) {
            View view = inflater.inflate(R.layout.house_chat_outgoing_message_holder, parent, false);
            return new OutgoingMessageHolder(view);
        } else if (viewType == MessageUi.INCOMING_MESSAGE) {
            View view = inflater.inflate(R.layout.house_chat_incoming_message_holder, parent, false);
            return new IncomingMessageHolder(view);
        } else {
            View view = inflater.inflate(R.layout.house_chat_header_holder, parent, false);
            return new HeaderHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (messageList == null) {
            return;
        }

        MessageUi messageUi = messageList.get(position);
        if (holder.getItemViewType() == MessageUi.OUTGOING_MESSAGE) {
            OutgoingMessageHolder outgoingMessageHolder = (OutgoingMessageHolder) holder;
            outgoingMessageHolder.setMessageContent(messageUi.getMessage().getContent());
            outgoingMessageHolder.setMessageTime(messageUi.getMessage().getSendTimestamp());
        } else if (holder.getItemViewType() == MessageUi.INCOMING_MESSAGE) {
            IncomingMessageHolder incomingMessageHolder = (IncomingMessageHolder) holder;
            incomingMessageHolder.setMessageSender(messageUi.getSenderName(), messageUi.getSenderColor());
            incomingMessageHolder.setMessageContent(messageUi.getMessage().getContent());
            incomingMessageHolder.setMessageTime(messageUi.getMessage().getSendTimestamp());
        } else {
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.setHeaderTitle(messageUi.getHeaderTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getMessageType();
    }

    @Override
    public int getItemCount() {
        if (messageList == null) {
            return 0;
        }

        return messageList.size();
    }

    public void setMessageList(List<MessageUi> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

}
