package com.example.roomie.house.groceries.grocery;

import java.util.Date;

public class Grocery {

    private String _id;
    private String _name;
    private int _size;
    private Date _creationDate;
    private Date _lastUpdateDate;

    Grocery(){

    }

    Grocery(String name, int size) {
        _name = name;
        _size = size;
        _creationDate = new Date();
        _lastUpdateDate = _creationDate;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_size() {
        return _size;
    }

    public void set_size(int _size) {
        this._size = _size;
    }

    public Date get_lastUpdateDate() {
        return _lastUpdateDate;
    }

    public void set_lastUpdateDate(Date _lastUpdateDate) {
        this._lastUpdateDate = _lastUpdateDate;
    }

    public Date get_creationDate(){
        return _creationDate;
    }
}
