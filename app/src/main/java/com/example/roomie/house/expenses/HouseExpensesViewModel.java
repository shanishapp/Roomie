package com.example.roomie.house.expenses;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.roomie.FirestoreJob;
import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.chores.chore.newChoreJob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import static com.example.roomie.util.FirestoreUtil.CHORES_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.EXPENSES_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class HouseExpensesViewModel extends ViewModel implements ExpenseAdapter.OnExpenseListener,
        ExpenseAdapter.OnReceiptListener
{
    private FirebaseFirestore db;
    private MutableLiveData<List<Expense>> expenses;

    public HouseExpensesViewModel()
    {
        db = FirebaseFirestore.getInstance();
        expenses = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<CreateNewExpenseJob> setPayer(String expenseId, String payerID, String payerName, String houseId)
    {
        CreateNewExpenseJob expensesJob = new CreateNewExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<CreateNewExpenseJob> job = new MutableLiveData<>(expensesJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                .document(expenseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        Expense expense = task.getResult().toObject(Expense.class);
                        if (expense != null)
                        {
                            List<Expense> expenseList = expenses.getValue();
                            assert expenseList != null;
                            expenseList.remove(expense);
                            expense.set_payerID(payerID);
                            expense.set_payerName(payerName);
                            expenseList.add(expense);
                            expenses.setValue(expenseList);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                    .document(expenseId).update("_payerID", payerID);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                    .document(expenseId).update("_payerName", payerID);

                            expensesJob.setExpense(task.getResult().toObject(Expense.class));
                            expensesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.setValue(expensesJob);
                        }
                    } else
                    {
                        expensesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        expensesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(expensesJob);
                    }
                });

        return job;
    }

    public LiveData<CreateNewExpenseJob> deleteExpense(Expense expense, String houseId)
    {


        CreateNewExpenseJob expenseJob = new CreateNewExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<CreateNewExpenseJob> job = new MutableLiveData<>(expenseJob);

        expenses.getValue().remove(expense);
        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                .document(expense.get_id()).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                    } else
                    {
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        expenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    }
                    job.setValue(expenseJob);
                });
        return job;
    }

    public LiveData<AllExpensesJob> settleExpenses(String houseId)
    {
        AllExpensesJob expensesJob = new AllExpensesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllExpensesJob> job = new MutableLiveData<>(expensesJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                List<Expense> expenseList = expenses.getValue();
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                {
                    Expense expense = documentSnapshot.toObject(Expense.class);
                    assert expenseList != null;
                    expenseList.remove(expense);
                    assert expense != null;
                    String expensID = expense.get_id();
                    expense.settle();
                    expenseList.add(expense);
                    db.collection(HOUSES_COLLECTION_NAME)
                            .document(houseId)
                            .collection(EXPENSES_COLLECTION_NAME)
                            .document(expensID).update("_isSettled", true);
                }
                expenses.setValue(expenseList);
                expensesJob.setExpenses(expenseList);
                expensesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                job.setValue(expensesJob);
            } else
            {
                expensesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                expensesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                job.setValue(expensesJob);
            }
        });
        return job;
    }

    public LiveData<CreateNewExpenseJob>  settleExpense(Expense expense, String houseId) {
        CreateNewExpenseJob expenseJob = new CreateNewExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<CreateNewExpenseJob> job = new MutableLiveData<>(expenseJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                .document(expense.get_id())
                .get()
                .addOnCompleteListener(task ->  {
                    if(task.isSuccessful()) {
                        List<Expense> expenseList = expenses.getValue();
                        expense.settle();
                        expenses.setValue(expenseList);
                        db.collection(HOUSES_COLLECTION_NAME)
                                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                .document(expense.get_id()).update("_isSettled",true);
                        expenseJob.setExpense(task.getResult().toObject(Expense.class));
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(expenseJob);
                    } else {
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        expenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(expenseJob);
                    }
                });

        return job;
    }

    public LiveData<AllExpensesJob> getExpenses(String houseId)
    {
        AllExpensesJob expensesJob = new AllExpensesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllExpensesJob> job = new MutableLiveData<>(expensesJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                List<Expense> fetchedList = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                {
                    Expense expense = documentSnapshot.toObject(Expense.class);
                    fetchedList.add(expense);
                }
                expenses.setValue(fetchedList);
                expensesJob.setExpenses(fetchedList);
                expensesJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                job.setValue(expensesJob);
            } else
            {
                expensesJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                expensesJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                job.setValue(expensesJob);
            }
        });
        return job;
    }


    @Override
    public void onExpenseClick(int pos)
    {
        //TODO: implementation missing - delete/settle dialog
    }

    @Override
    public void onReceiptClick()
    {
        //TODO:implement
    }

    public void uploadReceiptImage(Intent data)
    {

        Uri uri
    }
}
