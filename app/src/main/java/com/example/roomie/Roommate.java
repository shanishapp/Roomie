package com.example.roomie;

import com.example.roomie.house.chores.Chore;
import com.example.roomie.house.expenses.Expense;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Roommate
{
    private String _name;
    private File _profilePic;
    private int _housePoints;
    private List<Chore> _chores;

    public String get_name()
    {
        return _name;
    }

    public File get_profilePic()
    {
        return _profilePic;
    }

    public int get_housePoints()
    {
        return _housePoints;
    }

    public List<Chore> get_chores()
    {
        return _chores;
    }
}
