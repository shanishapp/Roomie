package com.roomiemain.roomie.house.expenses;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.roomiemain.roomie.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BalanceBottomSheetDialog extends BottomSheetDialogFragment
{

    public static BalanceBottomSheetDialog newInstance()
    {
        return new BalanceBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.balance_bubble_linear, container,
                false);

        // get the views and attach the listener

        return view;

    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}