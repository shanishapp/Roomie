package com.example.roomie.house.chores.chore;

import android.util.Pair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class  Chore {
    private String _id;
    private Date _dueDate;
    private Date _creationDate;
    private Date _lastUpdatedDate;
    private Date _snoozeDate;//TODO need to be changed dynamically through the running
    private String _title;
    private String _description;
    private int _score;
    private String _assignee; //TODO change to residence class
    private boolean _showMenu;
    private boolean _choreDone;

    Chore(){}

    Chore(String title, String description, String assignee, Date dueDate, boolean showMenu, Date snoozeDate, int score) {
        _title = title;
        _description = description;
        _assignee = assignee;
        _dueDate = dueDate;
        _creationDate = new Date();
        _lastUpdatedDate = _creationDate;
        _snoozeDate = snoozeDate;
        _showMenu = showMenu;
        _score = score;
        _choreDone = false;
    }

    public String get_description() {
        if(_description == null)
            return "";
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public Date get_dueDate() {
        return _dueDate;
    }

    public boolean is_choreDone() {
        return _choreDone;
    }

    public void set_choreDone(boolean _choreDone) {
        this._choreDone = _choreDone;
    }

    public Date get_creationDate() {
        return _creationDate;
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

    public int get_score() {
        return _score;
    }

    public void set_score(int _score) {
        this._score = _score;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_assignee() {
        if(_assignee == null){
            return "";
        }
        return _assignee;
    }

    public void set_assignee(String _assignee) {
        this._assignee = _assignee;
    }

    public void set_title(String _title){
        this._title = _title;
    }

    public void set_dueDate(Date _dueDate) {
        this._dueDate = _dueDate;
//        String pattern = "dd/MM/yyyy HH:mm";
//        DateFormat df = new SimpleDateFormat(pattern);
//        try {
//            this._dueDate = df.parse(dueDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    public void setShowMenu(boolean bool) {
        this._showMenu = bool;
    }

    public boolean isShowMenu() {
        return _showMenu;
    }

    public void set_snoozeDate(Date snoozeDate){
        _snoozeDate = snoozeDate;
    }
}
