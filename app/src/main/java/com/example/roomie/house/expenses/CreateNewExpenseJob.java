package com.example.roomie.house.expenses;

import com.example.roomie.FirestoreJob;

public class CreateNewExpenseJob extends FirestoreJob
{
    private Expense expense;
    public CreateNewExpenseJob(FirestoreJob.JobStatus jobStatus)
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
