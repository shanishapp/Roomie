package com.example.roomie.house.chores;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.chores.chore.newChoreJob;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.roomie.util.FirestoreUtil.CHORESS_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class ChoreFragmentViewModel extends ViewModel {

    private FirebaseFirestore db;

    public ChoreFragmentViewModel(){
        db = FirebaseFirestore.getInstance();
    }

    private void getChoreById(String houseId, String choreId) {

        newChoreJob newChoreJob = new newChoreJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<com.example.roomie.house.chores.chore.newChoreJob> job = new MutableLiveData<>(newChoreJob);
        Chore chore;

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(CHORESS_COLLECTION_NAME)
                .whereEqualTo(FieldPath.documentId(),choreId)
                .get().addOnCompleteListener(task -> {
                    if( task.isSuccessful() ) {

                    }

                });
    }
    public void editChoreTitle(String houseId, String choreId, String newTitle) {

    }

    public void editChoreAssignee(String choreId, String newAssignee) {

    }

    public void editChoreDueDate(String choreId, String dueDate) {

    }
}
