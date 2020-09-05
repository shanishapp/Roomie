package com.example.roomie.house.expenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.roomie.House;
import com.example.roomie.R;
import com.example.roomie.Roommate;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.house.chores.chore.newChoreFragment;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.abhinay.input.CurrencyEditText;

public class NewExpenseFragment extends Fragment
{
    //TODO: remove roomateList, replace with "Roomie" class solution
    private static List<String> roommatesList = Arrays.asList("shani", "avihai", "uri");
    private NewExpenseViewModel newExpenseViewModel;
    private PowerSpinnerView expenseTypeSpinner;
    private PowerSpinnerView payerSpinner;
    private EditText customTitleEditText;
    private EditText contentEditText;
    private CurrencyEditText costEditText;
    private HouseActivityViewModel houseActivityViewModel;
    private House house;
    private Button createExpenseButton;
    private NavController navController;

    public NewExpenseFragment()
    {
    }


    //TODO: parameters
    public static newChoreFragment newInstance(String param1, String param2)
    {
        return new newChoreFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_new_chore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        //set up add button
        view.setOnClickListener(view1 -> {
            if (view1 != null)
            {
                createExpense(view);
            }
        });
    }


    private void initUI(View view)
    {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();
        createExpenseButton = view.findViewById(R.id.createExpenseBtn);
        navController = Navigation.findNavController(view);

        customTitleEditText = view.findViewById(R.id.customExpenseTypeEditText);
        expenseTypeSpinner = view.findViewById(R.id.expenseTypeSpinner);
        initTypeSpinner();

        payerSpinner = view.findViewById(R.id.expensePayerSpinner);
        initPayerSpinner();

        contentEditText = view.findViewById(R.id.expenseContentEditText);

        costEditText = view.findViewById(R.id.expenseCostEditText);


    }

    private void initPayerSpinner()
    {
        //TODO: change according to roommates
        String[] arr = {"shani", "avihai", "uri"};
        ArrayList<String> roommatesList = new ArrayList<>(Arrays.asList(arr));
        payerSpinner.setItems(roommatesList);
    }

    private void initTypeSpinner()
    {
        String[] arr = getAllExpenseTypes();
        ArrayList<String> expenseTypesList = new ArrayList<>(Arrays.asList(arr));
        expenseTypeSpinner.setItems(expenseTypesList);
        expenseTypeSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            if (s.equals("other") || s.equals("אחר"))
            {
                customTitleEditText.setVisibility(View.VISIBLE);
            } else
            {
                customTitleEditText.setText(s);
            }
        });
    }

    private void createExpense(View view)
    {
        //TODO: get type by enum and not string
        //TODO: clean up confusion with type and title
        Roommate payer;
        String title, content;
        Date dateCreated = new Date();
        title = getExpenseType();
        if (title == null) return;
        double cost = costEditText.getCleanDoubleValue();
        content = contentEditText.getText().toString();
        int idx = payerSpinner.getSelectedIndex();
        payer = new Roommate(roommatesList.get(idx));
        dateCreated = new Date();

        LiveData<CreateNewExpenseJob> job = newExpenseViewModel.createNewExpense(house, title, content, cost, payer,
                Expense.ExpenseType.GENERAL, dateCreated);

        job.observe(getViewLifecycleOwner(), createNewChoreJob -> {
            switch (createNewChoreJob.getJobStatus())
            {
                case IN_PROGRESS:
                    createExpenseButton.setEnabled(false);
                    break;
                case SUCCESS:
                    navController.navigate(R.id.action_choreFragment_to_house_chores_fragment_dest);
                    break;
                case ERROR:
                    createExpenseButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.create_new_chore_err_msg),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        });
    }

    private String getExpenseType()
    {
        String title;
        int idx = expenseTypeSpinner.getSelectedIndex();
        String selectedType = roommatesList.get(idx);
        if (selectedType.equals("GENERAL"))
        {
            title = customTitleEditText.getText().toString();
        } else
        {
            title = selectedType;

        }
        return title;
    }

    private String[] getAllExpenseTypes()
    {
        //TODO: find way to translate, add check for hebrew on getCostType
        return Arrays.stream(Objects.requireNonNull(Expense.ExpenseType.class.getEnumConstants())).map(Enum::name).toArray(String[]::new);
    }
}