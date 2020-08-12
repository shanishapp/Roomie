package com.example.roomie.house.chores;

import android.widget.ImageView;

public class OneChoreItem {

    private ImageView lockedUnlockedImage;
    private String choreTitle;
    private String choreDueDate;
    private String choreAssignee;

    public OneChoreItem(String title, String dueDate, String a) {
        choreTitle = title;
        choreDueDate = dueDate;
        choreAssignee = a;
    }

    public String getChoreTitle() {
        return choreTitle;
    }

    public String getChoreDueDate() {
        return choreDueDate;
    }

    public String getChoreAssignee() {
        return choreAssignee;
    }
}
