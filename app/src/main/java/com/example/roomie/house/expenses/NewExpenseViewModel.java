package com.example.roomie.house.expenses;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.example.roomie.House;
import com.example.roomie.Roommate;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;

import static com.example.roomie.util.FirestoreUtil.EXPENSES_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class NewExpenseViewModel extends ViewModel
{
    private FirebaseFirestore db;

    public NewExpenseViewModel()
    {
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<CreateNewExpenseJob> createNewExpense(House house, String title, String description, double cost,
                                                          Roommate roommate, Expense.ExpenseType type,
                                                          Date purchaseDate)
    {
        CreateNewExpenseJob newExpenseJob = new CreateNewExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<com.example.roomie.house.expenses.CreateNewExpenseJob> job =
                new MutableLiveData<>(newExpenseJob);
        Expense expense = new Expense(title, description, cost, purchaseDate, type, roommate);
        db.collection(HOUSES_COLLECTION_NAME).
                document(house.getId()).

                collection(EXPENSES_COLLECTION_NAME).add(expense).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                db.collection(HOUSES_COLLECTION_NAME)
                        .document(house.getId()).collection(EXPENSES_COLLECTION_NAME)
                        .whereEqualTo(FieldPath.documentId(), task.getResult().getId())
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful())
                            {
                                if (task1.getResult().isEmpty())
                                {
                                    newExpenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                    newExpenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                                    job.setValue(newExpenseJob);
                                    return;
                                }

                                newExpenseJob.setExpense(
                                        task1.getResult().getDocuments().get(0).toObject(Expense.class)

                                );
                                newExpenseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                                job.setValue(newExpenseJob);
                                setExpenseId(task.getResult().getId(), expense, house);
                            } else
                            {
                                newExpenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                                newExpenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                                job.setValue(newExpenseJob);
                            }
                        });
            } else
            {
                newExpenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                newExpenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);

                job.setValue(newExpenseJob);
            }
        });
        return job;
    }

    private void setExpenseId(String id, Expense expense, House house)
    {
        expense.set_id(id);
        db.collection(HOUSES_COLLECTION_NAME).document(house.getId()).collection(EXPENSES_COLLECTION_NAME).document(id).set(expense);
    }
}
