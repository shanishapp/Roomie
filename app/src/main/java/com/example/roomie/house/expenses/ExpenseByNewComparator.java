package com.example.roomie.house.expenses;

import java.util.Comparator;

public class ExpenseByNewComparator implements Comparator<Expense>
{
    @Override
    public int compare(Expense o1, Expense o2)
    {
        if (o2.get_creationDate().compareTo(o1.get_creationDate())>0)
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}