package com.example.roomie.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.example.roomie.User;
import com.example.roomie.house.chat.Message;
import com.example.roomie.splash.GetUserHouseJob;
import com.example.roomie.util.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.roomie.util.FirestoreUtil.CHAT_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

/**
 * This class functions as the single source of truth of all house info.
 */
public class HouseRepository {

    private static final String TAG = "HOUSE_REPOSITORY";

    private static HouseRepository instance = null;

    private FirebaseFirestore db;

    private ListenerRegistration chatMessagesListener;


    public static HouseRepository getInstance() {
        if (instance == null) {
            instance = new HouseRepository();
        }

        return instance;
    }

    private HouseRepository() {
        db = FirebaseFirestore.getInstance();
    }


    public LiveData<GetHouseRoomiesJob> getHouseRoomies(String houseId) {
        GetHouseRoomiesJob getHouseRoomiesJob = new GetHouseRoomiesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<GetHouseRoomiesJob> job = new MutableLiveData<>(getHouseRoomiesJob);

        // get roomies UIDs
        db.collection(FirestoreUtil.HOUSES_COLLECTION_NAME)
                .whereEqualTo(FieldPath.documentId(), houseId)
                .get()
                .addOnSuccessListener(task -> {
                    int numberHousesFetched = task.getDocuments().size();
                    if (numberHousesFetched == 1) {
                        House house = task.getDocuments().get(0).toObject(House.class);
                        Map<String, House.Roles> houseRoomiesMap = house.getRoomies();
                        List<String> houseRoomiesUidList = new ArrayList<>(houseRoomiesMap.keySet());

                        // get actual roomies list
                        db.collection(FirestoreUtil.USERS_COLLECTION_NAME)
                                .whereIn("uid", houseRoomiesUidList)
                                .get()
                                .addOnSuccessListener(task1 -> {
                                    List<User> roomiesList = task1.toObjects(User.class);
                                    getHouseRoomiesJob.setRoomiesList(roomiesList);
                                    getHouseRoomiesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                    job.setValue(getHouseRoomiesJob);
                                })
                                .addOnFailureListener(task1 -> {
                                    getHouseRoomiesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    getHouseRoomiesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(getHouseRoomiesJob);
                                    Log.d(TAG, "Error fetching the roomies in getHouseRoomies.", task1.getCause());
                                });
                    } else {
                        getHouseRoomiesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        getHouseRoomiesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(getHouseRoomiesJob);
                        Log.d(TAG, "Invalid number of houses fetched (" + numberHousesFetched + ").");
                    }
                })
                .addOnFailureListener(task -> {
                    getHouseRoomiesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    getHouseRoomiesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(getHouseRoomiesJob);
                    Log.d(TAG, "Error fetching the house in getHouseRoomies.", task.getCause());
                });

        return job;
    }

    public LiveData<FirestoreJob> updateHouseInfo(String houseId, String name, String address, String desc) {
        FirestoreJob firestoreJob = new FirestoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<FirestoreJob> job = new MutableLiveData<>(firestoreJob);

        db.collection(FirestoreUtil.HOUSES_COLLECTION_NAME)
                .document(houseId)
                .update("name", name, "address", address, "desc", desc)
                .addOnSuccessListener(task -> {
                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                    job.setValue(firestoreJob);
                })
                .addOnFailureListener(task -> {
                    firestoreJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    firestoreJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(firestoreJob);
                    Log.d(TAG, "Error during house info update.", task.getCause());
                });

        return job;
    }

    public LiveData<GetUserHouseJob> getUserHouse(String userId) {
        GetUserHouseJob getUserHouseJob = new GetUserHouseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<GetUserHouseJob> job = new MutableLiveData<>(getUserHouseJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .whereGreaterThan("roomies." + userId, "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 1) {
                            getUserHouseJob.setUserHasHouse(true);
                            getUserHouseJob.setHouse(
                                    task.getResult().getDocuments().get(0).toObject(House.class)
                            );
                            getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        } else if (task.getResult().size() == 0) {
                            getUserHouseJob.setUserHasHouse(false);
                            getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        } else {
                            // found multiple houses - this should not happen
                            getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                            // TODO change to a specific error
                            getUserHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        }

                        job.setValue(getUserHouseJob);
                    } else {
                        getUserHouseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        getUserHouseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                        job.setValue(getUserHouseJob);

                        Log.d(TAG, "Error while fetching the user house: ", task.getException());
                    }
                });

        return job;
    }

    public LiveData<SendMessageJob> sendMessage(String houseId, String senderId, String messageContent) {
        SendMessageJob sendMessageJob = new SendMessageJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<SendMessageJob> job = new MutableLiveData<>(sendMessageJob);

        Message message = new Message();
        message.setSender(senderId);
        message.setContent(messageContent);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId)
                .collection(CHAT_COLLECTION_NAME)
                .add(message)
                .addOnSuccessListener(task -> {
                    // fetch the sent message
                    db.collection(HOUSES_COLLECTION_NAME)
                            .document(houseId)
                            .collection(CHAT_COLLECTION_NAME)
                            .whereEqualTo(FieldPath.documentId(), task.getId())
                            .get()
                            .addOnSuccessListener(task1 -> {
                                if (task1.getDocuments().size() != 1) {
                                    sendMessageJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    sendMessageJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                    job.setValue(sendMessageJob);

                                    Log.d(TAG, "Error message not unique / doesn't exist");
                                } else {
                                    Message msg = task1.getDocuments().get(0).toObject(Message.class);
                                    sendMessageJob.setMessage(msg);
                                    sendMessageJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                    job.setValue(sendMessageJob);
                                }
                            })
                            .addOnFailureListener(task1 -> {
                                sendMessageJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                sendMessageJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                                job.setValue(sendMessageJob);

                                Log.d(TAG, "Error while fetching the sent message.", task1.getCause());
                            });
                })
                .addOnFailureListener(task -> {
                    sendMessageJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    sendMessageJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(sendMessageJob);

                    Log.d(TAG, "Error while sending the message.", task.getCause());
                });

        return job;
    }

    public LiveData<GetMessagesJob> getMessages(String houseId) {
        GetMessagesJob getMessagesJob = new GetMessagesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<GetMessagesJob> job = new MutableLiveData<>(getMessagesJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId)
                .collection(CHAT_COLLECTION_NAME)
                .orderBy("sendTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(task -> {
                    List<Message> messageList = task.toObjects(Message.class);
                    getMessagesJob.setMessageList(messageList);
                    getMessagesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                    job.setValue(getMessagesJob);
                })
                .addOnFailureListener(task -> {
                    getMessagesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    getMessagesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(getMessagesJob);

                    Log.d(TAG, "Error while fetching the messages.", task.getCause());
                });

        return job;
    }

    public LiveData<GetMessagesJob> listenForMessages(String houseId) {
        GetMessagesJob getMessagesJob = new GetMessagesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<GetMessagesJob> job = new MutableLiveData<>(getMessagesJob);

        Query getMessagesQuery = db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId)
                .collection(CHAT_COLLECTION_NAME);

        chatMessagesListener = getMessagesQuery.orderBy("sendTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) ->  {
                    if (e != null) {
                        getMessagesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        getMessagesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(getMessagesJob);

                        Log.d(TAG, "Error listening to chat messages.", e);
                        return;
                    }

                    List<Message> messageList = queryDocumentSnapshots.toObjects(Message.class);
                    getMessagesJob.setMessageList(messageList);
                    getMessagesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                    job.setValue(getMessagesJob);
                });

        return job;
    }

    public void stopListeningForMessages() {
        if (chatMessagesListener == null) {
            return;
        }

        chatMessagesListener.remove();
    }
}
