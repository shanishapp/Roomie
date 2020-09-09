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
    private Pair<String,Integer> _kind;
    private String _assignee; //TODO change to residence class
    private boolean _showMenu;

    Chore(){}

    Chore(String title, String assignee, Date dueDate, boolean showMenu) {
        _title = title;
        _assignee = assignee;
        _dueDate = dueDate;
        _creationDate = new Date();
        _lastUpdatedDate = _creationDate;
        _snoozeDate = null;
        _showMenu = showMenu;
    }

    public Date get_dueDate() {
        return _dueDate;
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
}
