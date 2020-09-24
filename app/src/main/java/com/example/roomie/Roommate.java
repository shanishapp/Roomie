package com.example.roomie;

public class Roommate
{
    //TODO: profile pic
    private String _userID;
    private String _name;

    public Roommate()
    {
    }

    public Roommate(String name, String ID)
    {
        _name = name;
        _userID = ID;
    }

    public String get_name()
    {
        return _name;
    }

    public String get_userID()
    {
        return _userID;
    }
}
