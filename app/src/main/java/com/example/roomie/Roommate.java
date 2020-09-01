package com.example.roomie;

import com.example.roomie.house.chores.chore.Chore;

import java.util.ArrayList;
import java.util.List;

public class Roommate
{
    //TODO: profile pic, chores?
    private String _name;
    private int _housePoints;
    private List<Chore> _chores;

    public Roommate(String name)
    {
        _name = name;
        _housePoints = 0;
        _chores = new ArrayList<>();
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
