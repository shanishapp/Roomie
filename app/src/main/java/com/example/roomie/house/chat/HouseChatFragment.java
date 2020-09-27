package com.example.roomie.house.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.roomie.R;
import com.example.roomie.User;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetMessagesJob;
import com.example.roomie.repositories.SendMessageJob;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseChatFragment extends Fragment {

    public static final String[] USER_COLORS = { "#FF5733", "#900C3F", "#0C901B", "#905D0C", "#3587A4", "042A2B", "#F991CC", "#FFC300"};

    private HouseActivityViewModel houseActivityViewModel;

    private HouseChatViewModel houseChatViewModel;

    private RecyclerView messagesRecyclerView;

    private EditText messageInput;

    private ImageButton sendButton;

    private RecyclerView.LayoutManager layoutManager;

    private MessageAdapter messageAdapter;


    public HouseChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseChatFragment.
     */
    public static HouseChatFragment newInstance() {
        return new HouseChatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        houseChatViewModel = new ViewModelProvider(requireActivity()).get(HouseChatViewModel.class);
        houseChatViewModel.setHouseId(houseActivityViewModel.getHouse().getId());
        setUIElements(view);
        setListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        setContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        houseChatViewModel.stopListeningForMessages();
    }

    private void setUIElements(View view) {
        messagesRecyclerView = view.findViewById(R.id.house_chat_recycler_view);
        messageInput = view.findViewById(R.id.house_chat_message_input);
        sendButton = view.findViewById(R.id.house_chat_send_button);

        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(getContext(), houseActivityViewModel.getRoomiesList());
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void setListeners() {
        sendButton.setOnClickListener(this::sendMessage);
    }

    private void setContent() {
        LiveData<GetMessagesJob> job = houseChatViewModel.listenForMessages();
        job.observe(getViewLifecycleOwner(), getMessagesJob -> {
            switch (getMessagesJob.getJobStatus()) {
                case SUCCESS:
                    List<Message> messageList = getMessagesJob.getMessageList();
                    List<MessageUi> uiMessageList = uifyMessageList(messageList);
                    messageAdapter.setMessageList(uiMessageList);
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Error updating messages.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        });
    }

    private List<MessageUi> uifyMessageList(List<Message> messageList) {
        List<MessageUi> uifiedMessageList = new ArrayList<>();
        if (messageList.isEmpty()) {
            return uifiedMessageList;
        }

        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (int i = 0; i < messageList.size(); i++) {
            Message curMessage = messageList.get(i);
            MessageUi messageUi = new MessageUi();

            int messageType = (curMessage.getSender().equals(myUid)) ? MessageUi.OUTGOING_MESSAGE : MessageUi.INCOMING_MESSAGE;
            int roomieIndex = houseActivityViewModel.getRoomieIndexById(curMessage.getSender());
            if (roomieIndex < 0) {
                roomieIndex = 0;
            }

            messageUi.setMessageType(messageType);
            messageUi.setSenderColor(USER_COLORS[roomieIndex]);
            messageUi.setSenderName(houseActivityViewModel.getRoomieNameById(curMessage.getSender()));
            messageUi.setMessage(curMessage);

            uifiedMessageList.add(messageUi);

            // check if need to add a header
            MessageUi headerUi = getHeaderUi(messageList, i, curMessage);
            if (headerUi != null) {
                uifiedMessageList.add(headerUi);
            }
        }

        return uifiedMessageList;
    }

    private MessageUi getHeaderUi(List<Message> messageList, int curPos, Message curMessage) {
        if (curMessage.getSendTimestamp() == null) {
            return null; // cur message not yet updated with its timestamp
        }

        if (curPos + 1 >= messageList.size()) {
            // if we reached the end of the message list we need to add the date header
            MessageUi headerUi = new MessageUi();
            headerUi.setMessageType(MessageUi.HEADER);
            headerUi.setHeaderTitle(getDateString(curMessage.getSendTimestamp()));

            return headerUi;
        }

        Message nextMessage = messageList.get(curPos + 1);
        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String curMessageDate = localDateFormat.format(curMessage.getSendTimestamp());
        String nextMessageDate = localDateFormat.format(nextMessage.getSendTimestamp());
        if (!curMessageDate.equals(nextMessageDate)) {
            // add header only if the next message is of different day from cur message
            MessageUi headerUi = new MessageUi();
            headerUi.setMessageType(MessageUi.HEADER);
            headerUi.setHeaderTitle(getDateString(curMessage.getSendTimestamp()));

            return headerUi;
        }

        return null;
    }

    private String getDateString(Date timestamp) {
        long now = Instant.now().toEpochMilli();
        long today = now - (now % (1000 * 60 * 60 * 24));
        long yesterday = today - (1000 * 60 * 60 * 24);

        Date todayDate = new Date(today);
        Date yesterdayDate = new Date(yesterday);
        if (timestamp.after(todayDate) || timestamp.equals(todayDate)) {
            return getString(R.string.house_chat_header_today);
        }

        if (timestamp.after(yesterdayDate) || timestamp.equals(yesterdayDate)) {
            return getString(R.string.house_chat_header_yesterday);
        }

        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return localDateFormat.format(timestamp);

    }

    private void sendMessage(View view) {
        String messageContent = messageInput.getText().toString();
        if (messageContent.isEmpty()) {
            return;
        }
        messageInput.setText("");

        LiveData<SendMessageJob> job = houseChatViewModel.sendMessage(messageContent);
        job.observe(getViewLifecycleOwner(), sendMessageJob -> {
            switch (sendMessageJob.getJobStatus()) {
                case SUCCESS:
                    break;
                case ERROR:
                    Toast.makeText(getContext(), "Error while sending the message, please try again.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    // update the chat
                    break;
            }
        });
    }
}