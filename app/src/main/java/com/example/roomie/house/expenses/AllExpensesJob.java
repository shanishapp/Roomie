package com.example.roomie.house.expenses;

import com.example.roomie.FirestoreJob;

import java.util.List;

public class AllExpensesJob extends FirestoreJob
{
    private List<Expense> expenses;

    public AllExpensesJob()
    {
        super();
    }

    public AllExpensesJob(FirestoreJob.JobStatus jobStatus)
    {
        super(jobStatus);
    }
    public AllExpensesJob(FirestoreJob.JobStatus jobStatus, FirestoreJob.JobErrorCode errorCode)
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
