package com.example.roomie.house.expenses;

import android.graphics.drawable.Drawable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Expense
{
    private static final String TYPE_PROFESSIONAL_STRING = "Professional";
    private static final String TYPE_GROCERY_SHOPPING_STRING = "Grocery Shopping";
    private static final String TYPE_BILL_STRING = "Bill";
    private static final String TYPE_GENERAL_STRING = "General";

    private String _id;
    private String _title;
    private boolean _isSettled;
    private String _description;
    private ExpenseType _type;
    private double _cost;
    //TODO: maybe only one date needed? Maybe none? Maybe display?
    private Date _creationDate;
    private Date _purchaseDate;
    private Drawable receiptImage;
    private String _payerName;
    private String _payerID;
    private boolean _hasReceipt;

    public static ExpenseType typeFromString(String title)
    {
        ExpenseType resultType = null;
        switch (title)
        {
            case TYPE_PROFESSIONAL_STRING:
                resultType = ExpenseType.PROFESSIONAL;
                break;
            case TYPE_GROCERY_SHOPPING_STRING:
                resultType = ExpenseType.GROCERIES;
                break;
            case TYPE_BILL_STRING:
                resultType = ExpenseType.BILL;
                break;
            default:
                resultType = ExpenseType.GENERAL;
        }
        return resultType;
    }

    public static List<String> getExpenseTypes()
    {
        String[] arr = {TYPE_BILL_STRING, TYPE_GROCERY_SHOPPING_STRING, TYPE_PROFESSIONAL_STRING,
                TYPE_GENERAL_STRING};
        return Arrays.asList(arr);
    }

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

    public void settle()
    {
        _isSettled = true;
    }

    public boolean is_isSettled()
    {
        return _isSettled;
    }


    public enum ExpenseType
    {
        PROFESSIONAL,
        GROCERIES,
        BILL,
        GENERAL
    }

    public Expense()
    {

    }

    public Expense(String name, String description, double cost, Date purchaseDate,
                   ExpenseType type, String payerID, String payerName, boolean isSettled)
    {
        _title = name;
        _description = description;
        _type = type;
        _cost = cost;
        _purchaseDate = purchaseDate;
        _creationDate = new Date();
        _payerID = payerID;
        _payerName = payerName;
        _isSettled = false;
        boolean hasReceiptImage = false;
        _isSettled = isSettled;

    }


    public Expense(String name, String description, double cost, Date purchaseDate,
                   ExpenseType type, String payerID, String payerName)
    {
        _title = name;
        _description = description;
        _type = type;
        _cost = cost;
        _purchaseDate = purchaseDate;
        _creationDate = new Date();
        _payerID = payerID;
        _payerName = payerName;
        _isSettled = false;
        boolean hasReceiptImage = false;
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

    public double get_cost()
    {
        return _cost;
    }


    public String get_payerID()
    {
        return _payerID;
    }

    public String get_description()
    {
        return _description;
    }

    public String get_title()
    {
        return _title;
    }

    public void set_cost(float _cost)
    {
        this._cost = _cost;
    }

    public void set_description(String _description)
    {
        this._description = _description;
    }

    public void set_title(String _title)
    {
        this._title = _title;
    }


    public void set_payerName(String _payerName)
    {
        this._payerName = _payerName;
    }

    public String get_payerName()
    {
        return _payerName;
    }

    public void set_payerID(String _payerID)
    {
        this._payerID = _payerID;
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
