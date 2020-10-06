package com.roomiemain.roomie.house.expenses;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A class representing an expense made by a roommate.
 */

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
    private Date _creationDate;
    private String _receiptImageUriString;
    private String _payerName;
    private String _payerID;
    private boolean _hasReceipt;


    /**
     * A helper method which converts a string to an {@link ExpenseType} enum.
     *
     * @param title - a string of an expense title.
     * @return The corresponding {@link ExpenseType}, or GENERAL if non matches.
     */
    public static ExpenseType typeFromString(String title)
    {
        ExpenseType resultType;
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

    /**
     * @return all possible strings matching expense types.
     */

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

    public String get_receiptImageUriString()
    {
        return _receiptImageUriString;
    }

    public void set_receiptImageUriString(String _receiptImageUriString)
    {
        this._receiptImageUriString = _receiptImageUriString;
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

    public Expense(String _id, String _title, boolean _isSettled, String _description, ExpenseType _type,
                   double _cost, Date _creationDate, String _payerName,
                   String _payerID, boolean _hasReceipt)
    {
        this._id = _id;
        this._title = _title;
        this._isSettled = _isSettled;
        this._description = _description;
        this._type = _type;
        this._cost = _cost;
        this._creationDate = _creationDate;
        this._payerName = _payerName;
        this._payerID = _payerID;
        this._hasReceipt = _hasReceipt;
    }

    public Expense(String _id, String _title, boolean _isSettled, String _description, ExpenseType _type,
                   double _cost, Date _creationDate, String _receiptImageUri, String _payerName,
                   String _payerID, boolean _hasReceipt)
    {
        this._id = _id;
        this._title = _title;
        this._isSettled = _isSettled;
        this._description = _description;
        this._type = _type;
        this._cost = _cost;
        this._creationDate = _creationDate;
        _receiptImageUriString = _receiptImageUri;
        this._payerName = _payerName;
        this._payerID = _payerID;
        this._hasReceipt = _hasReceipt;
    }

    public Expense(String name, String description, double cost,
                   ExpenseType type, String payerID, String payerName, Date creationDate)
    {
        _title = name;
        _description = description;
        _type = type;
        _cost = cost;
        _creationDate = creationDate;
        _payerID = payerID;
        _payerName = payerName;
        _isSettled = false;
        _hasReceipt = false;
    }

    public Expense(String name, String description, double cost,
                   ExpenseType type, String payerID, String payerName, Timestamp creationTimeStamp)
    {
        _title = name;
        _description = description;
        _type = type;
        _cost = cost;
        _creationDate = creationTimeStamp;
        _payerID = payerID;
        _payerName = payerName;
        _isSettled = false;
        _hasReceipt = false;
    }

    public Expense(String name, String description, double cost,
                   ExpenseType type, String payerID, String payerName, boolean isSettled)
    {
        _title = name;
        _description = description;
        _type = type;
        _cost = cost;
        _creationDate = new Date();
        _payerID = payerID;
        _payerName = payerName;
        _isSettled = false;
        _hasReceipt = false;
        _isSettled = isSettled;
    }


    public Expense(String name, String description, double cost,
                   ExpenseType type, String payerID, String payerName)
    {
        _title = name;
        _description = description;
        _type = type;
        _cost = cost;
        _creationDate = new Date();
        _payerID = payerID;
        _payerName = payerName;
        _isSettled = false;
        _hasReceipt = false;
    }


    public Date get_creationDate()
    {
        return _creationDate;
    }

    public void set_creationDate(Date _creationDate)
    {
        this._creationDate = _creationDate;
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
}
