package com.example.roomie;

import com.example.roomie.house.chores.chore.Chore;

import java.util.List;

public class Roommate
{
    //TODO: profile pic
    private String _name;
    private int _housePoints;
    private List<Chore> _chores;

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
