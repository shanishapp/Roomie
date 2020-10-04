package com.example.roomie.house.expenses;

import android.net.Uri;
import android.preference.PreferenceFragment;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.roomie.FirestoreJob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import static com.example.roomie.util.FirestoreUtil.EXPENSES_COLLECTION_NAME;
import static com.example.roomie.util.FirestoreUtil.HOUSES_COLLECTION_NAME;

public class HouseExpensesViewModel extends ViewModel implements ExpenseAdapter.OnExpenseListener
{
    public static final String CREATION_DATE_FIELD_NAME = "_creationDate";
    public static final String PAYER_ID_FIELD_NAME = "_payerId";
    public static final String IS_SETTLED_FIELD_NAME = "_isSettled";
    public static final String PAYER_NAME_FIELD_NAME = "_payerName";
    public static final String RECEIPT_IMAGE_URI_FIELD_NAME = "_receiptImageUriString";
    public static final String HAS_RECEIPT_FIELD_NAME = "_hasReceipt";
    public static final String ID_FIELD_NAME = "_id";
    private static final String TAG = "HOUSE_EXPENSES";

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private MutableLiveData<List<Expense>> expenses;
    private MutableLiveData<Uri> receiptPhoto;

    public HouseExpensesViewModel()
    {
        db = FirebaseFirestore.getInstance();
        expenses = new MutableLiveData<>(new ArrayList<>());

    }

    public LiveData<ExpenseJob> setPayer(String expenseId, String payerID, String payerName, String houseId)
    {
        ExpenseJob expensesJob = new ExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<ExpenseJob> job = new MutableLiveData<>(expensesJob);

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
                                    .document(expenseId).update(PAYER_ID_FIELD_NAME, payerID);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                    .document(expenseId).update(PAYER_NAME_FIELD_NAME, payerName);

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

    public LiveData<ExpenseJob> deleteExpense(Expense expense, String houseId)
    {


        ExpenseJob expenseJob = new ExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<ExpenseJob> job = new MutableLiveData<>(expenseJob);

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
                            .document(expensID).update(IS_SETTLED_FIELD_NAME, true);
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

    public LiveData<ExpenseJob> settleExpense(Expense expense, String houseId)
    {
        ExpenseJob expenseJob = new ExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<ExpenseJob> job = new MutableLiveData<>(expenseJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                .document(expense.get_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        List<Expense> expenseList = expenses.getValue();
                        expense.settle();
                        expenses.setValue(expenseList);
                        db.collection(HOUSES_COLLECTION_NAME)
                                .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                .document(expense.get_id()).update(IS_SETTLED_FIELD_NAME, true);
                        expenseJob.setExpense(task.getResult().toObject(Expense.class));
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                        job.setValue(expenseJob);
                    } else
                    {
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

    public LiveData<AllExpensesJob> getUnSettledExpenses(String houseId)
    {
        AllExpensesJob expensesJob = new AllExpensesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllExpensesJob> job = new MutableLiveData<>(expensesJob);

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME).whereEqualTo(IS_SETTLED_FIELD_NAME, false)
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

    public LiveData<AllExpensesJob> getExpensesFromLastWeek(String houseId)
    {
        AllExpensesJob expensesJob = new AllExpensesJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<AllExpensesJob> job = new MutableLiveData<>(expensesJob);
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        Date start = c.getTime();

        db.collection(HOUSES_COLLECTION_NAME)
                .document(houseId).collection(EXPENSES_COLLECTION_NAME).whereGreaterThan(CREATION_DATE_FIELD_NAME,
                start)

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


    public LiveData<ExpenseJob> updateReceiptImageUri(String houseId, String expenseId, String uri)
    {
        ExpenseJob expenseJob = new ExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<ExpenseJob> job = new MutableLiveData<>(expenseJob);

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
                            expenseList.remove(expense);
                            expense.set_receiptImageUriString(uri);
                            expense.set_hasReceipt(true);
                            expenseList.add(expense);
                            expenses.setValue(expenseList);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                    .document(expenseId).update(RECEIPT_IMAGE_URI_FIELD_NAME, uri);
                            db.collection(HOUSES_COLLECTION_NAME)
                                    .document(houseId).collection(EXPENSES_COLLECTION_NAME)
                                    .document(expenseId).update(HAS_RECEIPT_FIELD_NAME, true);

                            expenseJob.setExpense(task.getResult().toObject(Expense.class));
                            expenseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                            job.setValue(expenseJob);
                        }
                    } else
                    {
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        expenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(expenseJob);
                    }
                });

        return job;
    }

    public LiveData<ExpenseJob> getExpenseById(String expenseId)
    {
        ExpenseJob expenseJob = new ExpenseJob(FirestoreJob.JobStatus.IN_PROGRESS);
        MutableLiveData<ExpenseJob> job = new MutableLiveData<>(expenseJob);

        db.collection(EXPENSES_COLLECTION_NAME)
                .whereEqualTo(ID_FIELD_NAME, expenseId)
                .get()
                .addOnSuccessListener(task -> {
                    if (task.isEmpty() || task.size() > 1)
                    {
                        expenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                        expenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                        job.setValue(expenseJob);
                        Log.d(TAG, "Error too many or no records found (" + task.size() + ") in getExpenseById.");
                        return;
                    }

                    Expense expense = task.getDocuments().get(0).toObject(Expense.class);
                    expenseJob.setExpense(expense);
                    expenseJob.setJobStatus(FirestoreJob.JobStatus.SUCCESS);
                    job.setValue(expenseJob);
                })
                .addOnFailureListener(task -> {
                    expenseJob.setJobStatus(FirestoreJob.JobStatus.ERROR);
                    expenseJob.setJobErrorCode(FirestoreJob.JobErrorCode.GENERAL);
                    job.setValue(expenseJob);
                    Log.d(TAG, "Error while fetching the expense in getExpenseById", task.getCause());
                });
        return job;
    }

}
