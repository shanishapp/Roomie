package com.example.roomie.house.chores;

import android.util.Pair;

import java.util.Date;

public class Chore {
    private String _id;
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



    public Date get_dueDate() {
        return _dueDate;
    }

    public Date get_lastUpdatedDate() {
        return _lastUpdatedDate;
    }

    public Date get_snoozeDate() {
        return _snoozeDate;
    }

    public String get_title() {
        return _title;
    }

    public String get_description() {
        return _description;
    }

    public Pair<String, Integer> get_kind() {
        return _kind;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_assignee() {
        return _assignee;
    }

    public void set_assignee(String _assignee) {
        this._assignee = _assignee;
    }
}
