package com.example.roomie;

import android.util.Pair;

import java.util.Date;

public class Chore {
    private Date _dueDate;
    private Date _creationDate;
    private Date _lastUpdatedDate;
    private Date _snoozeDate;//TODO need to be changed dynamically through the running

    private String _title;
    private String _description;
    private Pair<String,Integer> _kind;
    private String _assignee; //TODO change to residence class

    Chore(String title, String description, Pair<String, Integer> kind, String assignee, Date dueDate) {
        _title = title;
        _description = description;
        _kind = kind;
        _assignee = assignee;
        _dueDate = dueDate;
        _creationDate = new Date();
        _lastUpdatedDate = _creationDate;
        _snoozeDate = null;
    }
}
