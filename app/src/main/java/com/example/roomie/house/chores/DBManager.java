package com.example.roomie.house.chores;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class DBManager implements ChoreAdapter.OnChoreListener  {

    private FirebaseFirestore db;
    private ArrayList<Chore> choreList;
    public ChoreAdapter adapter = null;
    private CollectionReference collectionReference;
    private Context app = null;

    private static DBManager instance = new DBManager();
    private static final String CHORES_PATH = "chores";

    public static DBManager getInstance()
    {
        return instance;
    }

    private DBManager() {
        db = FirebaseFirestore.getInstance();
        choreList = new ArrayList<>();
        fillExampleList();
        adapter = new ChoreAdapter(choreList,this);
//        collectionReference = db.collection(CHORES_PATH);
//        collectionReference.addSnapshotListener((value, error) -> {
//            if (error != null) {
//                Log.w("error", "Listen failed.", error);
//                return;
//            } else {
//                choreList.clear();
//                for (QueryDocumentSnapshot snapshot : value) {
//                    Chore chore = snapshot.toObject(Chore.class);
//                    choreList.add(chore);
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    private void fillExampleList() {
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
        choreList.add(new Chore("chore", "important",null,"lucifer",new Date(6,6,6,5,4,3)));
    }


    public void addChore(final Chore chore) {
        choreList.add(chore);
        adapter.notifyDataSetChanged();

        collectionReference.add(chore).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                chore.set_id(documentReference.getId());
                collectionReference.document(chore.get_id()).update("id",chore.get_id());
            }
        });
    }

    public void lockChore(String choreId, String assignee) {
        collectionReference.document(choreId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Chore chore = documentSnapshot.toObject(Chore.class);
                if(chore !=null) {
                    choreList.remove(chore);
                    chore.set_assignee(assignee);
                    choreList.add(chore);
                    collectionReference.document(chore.get_id()).update("assignee",assignee)
                            .addOnSuccessListener(aVoid -> adapter.notifyDataSetChanged());
                }
            }
        });
    }

    public void unLockChore(String choreId) {
        collectionReference.document(choreId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Chore chore = documentSnapshot.toObject(Chore.class);
                if(chore != null && chore.get_assignee() != null) {
                    choreList.remove(chore);
                    chore.set_assignee(null);
                    choreList.add(chore);
                    collectionReference.document(chore.get_id()).update("assignee",null)
                            .addOnSuccessListener(aVoid -> adapter.notifyDataSetChanged());
                }
            }
        });
    }

    public void deleteChoreForever(Chore chore) {
        choreList.remove(chore);
        collectionReference.document(chore.get_id()).delete()
                .addOnSuccessListener(aVoid -> adapter.notifyDataSetChanged());
    }

    public ArrayList<Chore> loadAllChores() {
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            choreList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Chore todo = documentSnapshot.toObject(Chore.class);
                choreList.add(todo);
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.d("fail","failed load all chores"));
        return choreList;
    }

    public void setContext(Context applicationContext) {
        app = applicationContext;
    }

    @Override
    public void onChoreClick(int pos) {
        //TODO
    }

    public void setChoreData(String todoId, String newContent, Timestamp timestamp) {
        //TODO
    }


}