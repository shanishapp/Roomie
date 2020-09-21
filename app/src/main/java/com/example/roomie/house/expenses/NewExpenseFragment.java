package com.example.roomie.house.expenses;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.roomie.User;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.abhinay.input.CurrencyEditText;

//TODO: translate menu options to hebrew?

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.roomie.house.expenses.NewExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewExpenseFragment extends Fragment
{
    private NewExpenseViewModel newExpenseViewModel;
    private PowerSpinnerView titleSpinner;
    private EditText customTitleEditText;
    private EditText expenseDescription;
    private PowerSpinnerView payerSpinner;
    private CurrencyEditText priceEditText;
    private House house;
    private Button addExpenseButton;
    private NavController navController;
    private ArrayList<String> roommatesList;
    private Date date = new Date();
    private String title = null;
    private String payerName = null;

    public NewExpenseFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewExpenseFragment newInstance(String param1, String param2)
    {
        return new NewExpenseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        newExpenseViewModel = new ViewModelProvider(this).get(NewExpenseViewModel.class);
        roommatesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_create_expense, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        //set up add button
        addExpenseButton.setOnClickListener(view1 -> {
            if (view1 != null)
            {
                createExpense(view);
            }
        });
        loadRoommies(view);
    }

    private void initViews(View view)
    {
        HouseActivityViewModel houseActivityViewModel =
                new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        house = houseActivityViewModel.getHouse();
        addExpenseButton = view.findViewById(R.id.createExpenseBtn);
        navController = Navigation.findNavController(view);
        customTitleEditText = view.findViewById(R.id.customExpenseTypeEditText);
        expenseDescription = view.findViewById(R.id.expenseDescriptionEditText);
        titleSpinner = view.findViewById(R.id.expenseTypeSpinner);
        setupTypeSpinner();

        payerSpinner = view.findViewById(R.id.expensePayerSpinner);
        setupPayerSpinner();

        priceEditText = view.findViewById(R.id.expenseCostEditText);
        priceEditText.setDecimals(false);

        //TODO: change/pick currency, maybe in house settings
        String currencySymbol = getString(R.string.currency_sign);
        priceEditText.setCurrency(currencySymbol);
    }

    private void setupPayerSpinner()
    {

        payerSpinner.setItems(roommatesList);
        payerSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> payerName = s);
        payerSpinner.setSpinnerOutsideTouchListener((view, motionEvent) -> payerSpinner.dismiss());
        payerSpinner.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupTypeSpinner()
    {
        List<String> expenseTypes = Expense.getExpenseTypes();
        titleSpinner.setItems(expenseTypes);
        titleSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            titleSpinner.setError(null);
            title = s;
            if (s.equals(getString(R.string.general)))
            {
                customTitleEditText.setVisibility(View.VISIBLE);
            } else
            {
                customTitleEditText.setVisibility(View.GONE);
            }
        });

        titleSpinner.setSpinnerOutsideTouchListener((view, motionEvent) -> titleSpinner.dismiss());
        titleSpinner.setLifecycleOwner(getViewLifecycleOwner());

    }

    private void createExpense(View view)
    {
        Expense.ExpenseType type;
        if (title == null)
        {
            TextView errorText = (TextView) titleSpinner;
            errorText.setError("");
            errorText.setText(R.string.no_title_error_msg);//changes the selected item text to this
            return; //TODO error massage
        } else if (title.equals(getString(R.string.general)))
        {
            String diffT = customTitleEditText.getText().toString();
            if (!diffT.equals(""))
            {
                title = diffT;
            }
            type = Expense.ExpenseType.GENERAL;
        } else
        {
            type = Expense.typeFromString(title);
        }
        String description = expenseDescription.getText().toString();
        double cost = priceEditText.getCleanDoubleValue();


        LiveData<CreateNewExpenseJob> job = newExpenseViewModel.createNewExpense(house, title, description, cost,
                new Roommate(payerName), type, date);

        job.observe(getViewLifecycleOwner(), CreateNewExpenseJob -> {
            switch (CreateNewExpenseJob.getJobStatus())
            {
                case IN_PROGRESS:
                    addExpenseButton.setEnabled(false);
                    break;
                case SUCCESS:
                    navController.navigate(R.id.action_newExpenseFragment_to_house_expenses_fragment_dest);
                    break;
                case ERROR:
                    addExpenseButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.create_expense_error_msg),
                            Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        });
    }

    private void loadRoommies(View view)
    {
        LiveData<GetHouseRoomiesJob> job = HouseRepository.getInstance().getHouseRoomies(house.getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            switch (getHouseRoomiesJob.getJobStatus())
            {
                case SUCCESS:
                    for (User user : getHouseRoomiesJob.getRoomiesList())
                    {
                        roommatesList.add(user.getUsername());
                    }
                    //TODO: case FAILURE
            }
        });
    }
}