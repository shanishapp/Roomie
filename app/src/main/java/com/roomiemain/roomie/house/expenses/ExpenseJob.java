package com.roomiemain.roomie.house.expenses;

import com.roomiemain.roomie.FirestoreJob;

public class ExpenseJob extends FirestoreJob
{
    private Expense expense;

    public ExpenseJob()
    {
        super();
    }

    public ExpenseJob(FirestoreJob.JobStatus jobStatus, FirestoreJob.JobErrorCode errorCode)
    {
        super(jobStatus,errorCode);
    }

    public ExpenseJob(FirestoreJob.JobStatus jobStatus)
    {
        super(jobStatus);
    }

    public Expense getExpense()
    {
        return expense;
    }

    public void setExpense(Expense expense)
    {
        this.expense = expense;
    }
}
