package com.example.roomie.house.expenses;

import com.example.roomie.FirestoreJob;

import java.util.List;

public class allExpensesJob extends FirestoreJob
{
    private List<Expense> expenses;

    public allExpensesJob()
    {
        super();
    }

    public allExpensesJob(FirestoreJob.JobStatus jobStatus)
    {
        super(jobStatus);
    }
    public allExpensesJob(FirestoreJob.JobStatus jobStatus,FirestoreJob.JobErrorCode errorCode)
    {
        super(jobStatus,errorCode);
    }

    public List<Expense> getExpenses()
    {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses)
    {
        this.expenses = expenses;
    }
}
