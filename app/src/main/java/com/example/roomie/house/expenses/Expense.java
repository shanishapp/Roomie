package com.example.roomie.house.expenses;

import com.example.roomie.Roommate;

import java.util.Date;

public class Expense
{
    private String _id;
    private String _name;
    private String _description;
    private ExpenseType _type;
    private float _cost;
    private Date _creationDate;
    private Date _purchaseDate;
    private Roommate _payer;
    private boolean _hasReceipt;
    //TODO: receipt photo

    public boolean is_hasReceipt()
    {
        return _hasReceipt;
    }

    public void set_hasReceipt(boolean _hasReceipt)
    {
        this._hasReceipt = _hasReceipt;
    }

    public void set_id(String id)
    {
        _id = id;
    }

    public String get_id()
    {
        return _id;
    }

    public enum ExpenseType
    {
        PROFESSIONAL,
        GROCERIES,
        BILL,
        GENERAL
    }





    Expense(String name, String description, float cost, Date purchaseDate, ExpenseType type, Roommate payer)
    {
        _name = name;
        _description = description;
        _type = type;
        _cost = cost;
        _purchaseDate = purchaseDate;
        _creationDate = new Date();
        _payer = payer;
    }


    public Date getCreationDate()
    {
        return _creationDate;
    }

    public Date getPurchaseDate()
    {
        return _purchaseDate;
    }

    public ExpenseType get_type()
    {
        return _type;
    }

    public float get_cost()
    {
        return _cost;
    }

    public Roommate get_payer()
    {
        return _payer;
    }

    public String get_description()
    {
        return _description;
    }

    public String get_name()
    {
        return _name;
    }

    public void set_cost(float _cost)
    {
        this._cost = _cost;
    }

    public void set_description(String _description)
    {
        this._description = _description;
    }

    public void set_name(String _name)
    {
        this._name = _name;
    }

    public void set_payer(Roommate _payer)
    {
        this._payer = _payer;
    }

    public void set_type(ExpenseType _type)
    {
        this._type = _type;
    }
    //Todo: take picture
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private void dispatchTakePictureIntent () {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(myActivity.getPackageManager()) != null)
//        {
//            myActivity.startActivityForResult(takePictureIntent, 1);
//        }
//    }
}
