package com.example.roomie;

import com.example.roomie.house.chores.chore.Chore;

import java.util.ArrayList;
import java.util.List;

public class Roommate
{
    //TODO: profile pic
    private String _name;
    private int _housePoints;
    private double _totalExpenses;
    private List<Chore> _chores;

    public Roommate()
    {
    }

    public double get_totalExpenses()
    {
        return _totalExpenses;
    }

    public void addExpense(double cost)
    {
        _totalExpenses += cost;
    }

    public Roommate(String name)
    {
        _name = name;
        _housePoints = 0;
        _chores = new ArrayList<>();
        _totalExpenses = 0;
    }

    public String get_name()
    {
        return _name;
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
