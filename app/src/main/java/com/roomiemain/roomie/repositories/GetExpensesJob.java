package com.roomiemain.roomie.repositories;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.house.expenses.Expense;

import java.util.List;

public class GetExpensesJob extends FirestoreJob {

    private List<Expense> expenseList;


    public GetExpensesJob() {
        super();
    }

    public GetExpensesJob(JobStatus jobStatus) {
        super(jobStatus);
    }

    public GetExpensesJob(JobStatus jobStatus, JobErrorCode jobErrorCode) {
        super(jobStatus, jobErrorCode);
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public List<Expense> getExpenseList() {
        return expenseList;
    }
}
