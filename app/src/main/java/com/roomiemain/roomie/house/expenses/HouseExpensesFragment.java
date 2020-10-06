package com.roomiemain.roomie.house.expenses;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.MovableFloatingActionButton;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.User;
import com.roomiemain.roomie.house.HouseActivityViewModel;
import com.roomiemain.roomie.repositories.GetHouseRoomiesJob;
import com.roomiemain.roomie.repositories.HouseRepository;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseListener,
        ExpenseAdapter.OnReceiptListener
{

    private static final int READ_EXTERNAL_STORAGE_CODE = 2;

    private TextView myBalanceTextView, houseBalanceTextView;
    private RecyclerView.Adapter<ExpenseAdapter.ViewHolder> expenseAdapter = null;
    private BalanceDialogFragment balanceDialogFragment;
    private SettleExpensesDialogFragment settleExpensesDialogFragment;
    private AddReceiptDialog addReceiptDialog;
    private ReplaceReceiptDialog replaceReceiptDialog;
    private DeleteExpenseDialog deleteExpenseDialog;

    private StorageReference storageReference;


    private HouseActivityViewModel houseActivityViewModel;
    private HouseExpensesViewModel viewModel;
    private ArrayList<Expense> expenses;
    private int numberOfRoommates;
    private NavController navController;
    private FirebaseAuth auth;
    private Expense currentExpense;

    public HouseExpensesFragment()
    {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment houseExpensesFragment.
     */
    public static HouseExpensesFragment newInstance()
    {
        return new HouseExpensesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        storageReference = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        auth = FirebaseAuth.getInstance();
        View v = inflater.inflate(R.layout.fragment_house_expenses, container, false);
        houseActivityViewModel =
                new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        viewModel = new ViewModelProvider(this).get(HouseExpensesViewModel.class);
        LiveData<AllExpensesJob> job =
                viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            {
                setUpUI(allExpensesJob, v);
            }
        });
        balanceDialogFragment = BalanceDialogFragment.newInstance(this);
        settleExpensesDialogFragment = SettleExpensesDialogFragment.newInstance(this);
        addReceiptDialog = AddReceiptDialog.newInstance(this);
        replaceReceiptDialog = ReplaceReceiptDialog.newInstance(this);
        deleteExpenseDialog = DeleteExpenseDialog.newInstance(this);

        return v;
    }


    private void setUpUI(AllExpensesJob allExpensesJob, View view)
    {
        expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
        ExpenseByNewComparator s = new ExpenseByNewComparator();
        Collections.sort(expenses, s);
        expenseAdapter = new ExpenseAdapter(expenses, HouseExpensesFragment.this,
                HouseExpensesFragment.this, this);
        RecyclerView expensesRecyclerView = view.findViewById(R.id.expenses_recycler_view);
        expensesRecyclerView.setAdapter(expenseAdapter);
        expensesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        houseBalanceTextView = view.findViewById(R.id.house_balance_text);
        myBalanceTextView = view.findViewById(R.id.my_balance_amount_text);
        View balanceBubble = view.findViewById(R.id.balance_bubble);
        balanceBubble.setOnClickListener(v -> balanceDialogFragment.showDialog());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getRoommateNumber();
        navController = Navigation.findNavController(view);
        setUpAddExpenseButton(view);
        setUpSettleExpensesButton(view);
    }

    private void handleBalances()
    {
        String houseSpendingAmountString = String.valueOf(getHouseSpending());
        String houseSpendingString =
                getString(R.string.house_spending)
                        .concat(" ").concat(houseSpendingAmountString)
                        .concat(getString(R.string.currency_sign));
        houseBalanceTextView.setText(houseSpendingString);
        double myBalance = getBalanceByUid(auth.getUid());
        String myBalanceAmountString = String.valueOf(Math.abs(myBalance));
        String myBalancePrefixString;
        int color;
        if (myBalance < 0)
        {
            color = ContextCompat.getColor(getContext(), R.color.red);
            myBalancePrefixString = getString(R.string.you_owe);
        } else
        {
            color = ContextCompat.getColor(getContext(), R.color.green);
            myBalancePrefixString = getString(R.string.you_are_owed);
        }
        String myBalanceString = myBalancePrefixString
                .concat(" ")
                .concat(myBalanceAmountString)
                .concat(getString(R.string.currency_sign));
        myBalanceTextView.setTextColor(color);
        myBalanceTextView.setText(myBalanceString);
        myBalanceTextView.setVisibility(View.VISIBLE);
    }


    private void setUpSettleExpensesButton(View view)
    {
        Button settleExpensesButton = view.findViewById(R.id.settle_up_button);
        settleExpensesButton.setOnClickListener(view2 ->
        {
            if (view2 != null)
            {
                settleExpensesDialogFragment.showDialog();
            }
        });
    }

    private void setUpAddExpenseButton(View view)
    {
        MovableFloatingActionButton addExpenseButton = view.findViewById(R.id.expenses_fab);
        addExpenseButton.setOnClickListener(view1 -> {
            if (view1 != null)
            {
                navController.navigate(R.id.action_house_expenses_fragment_dest_to_newExpenseFragment);
            }
        });
    }

    private void settleExpenses()
    {
        for (Expense expense : expenses)
        {
            LiveData<ExpenseJob> settleJob = viewModel.settleExpense(expense,
                    houseActivityViewModel.getHouse().getId());
            settleJob.observe(getViewLifecycleOwner(), ExpenseJob -> {
                if (ExpenseJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
                {
                    //TODO: check if this is unnecessary
                    LiveData<AllExpensesJob> updateExpensesJob =
                            viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
                    viewModel.getExpenses(houseActivityViewModel.getHouse().getId());
                    updateExpensesJob.observe(getViewLifecycleOwner(), allExpensesJob -> {
                        if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
                        {
                            expenses = (ArrayList<Expense>) allExpensesJob.getExpenses();
                        }
                    });
                }
                expenseAdapter.notifyDataSetChanged();
                handleBalances();
            });
        }
    }

    @Override
    public void onExpenseClick(int pos)
    {
        deleteExpenseDialog.setPosition(pos);
        deleteExpenseDialog.showDialog();
    }

    @Override
    public void onReceiptClick(Expense expense)
    {
        currentExpense = expense;
        if (expense.is_hasReceipt())
        {
            replaceReceiptDialog.showDialog();
        } else
        {
            addReceiptDialog.showDialog();
        }
    }

    public double getHouseSpending()
    {
        double totalCost = 0;
        if (expenses != null)
        {
            for (Expense expense : expenses)
            {
                if (!expense.is_isSettled())
                {
                    totalCost += expense.get_cost();
                }
            }
        }
        return totalCost;
    }


    public double getSpendingByUid(String uid)
    {
        double spending = 0;
        for (Expense expense : expenses)
        {
            if (uid.equals(expense.get_payerID()) && !expense.is_isSettled())
            {
                spending += expense.get_cost();
            }
        }
        return spending;

    }

    public double getBalanceByUid(String uid)
    {
        double balance = getSpendingByUid(uid) - getHouseSpending() / numberOfRoommates;
        return Math.floor(balance * 100) / 100;
    }

    private void getRoommateNumber()
    {
        LiveData<GetHouseRoomiesJob> job =
                HouseRepository.getInstance().getHouseRoomies(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            switch (getHouseRoomiesJob.getJobStatus())
            {
                case SUCCESS:
                    numberOfRoommates = getHouseRoomiesJob.getRoomiesList().size();
                    handleBalances();
                case ERROR:
                    //TODO: implement
            }
        });
    }

    private void selectReceiptPicture()
    {
        boolean hasPermission = ActivityCompat
                .checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (hasPermission)
        {
            sendSelectReceiptPictureIntent();
        } else
        {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_CODE
            );
        }
    }

    private void sendSelectReceiptPictureIntent()
    {
        CropImage.activity().start(getContext(), this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                sendSelectReceiptPictureIntent();
            } else
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.request_permission_title);
                    alertDialog.setMessage(getString(R.string.request_read_external_storage_msg));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.request_permission_approve),
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK)
            {
                updateReceiptImage(currentExpense.get_id(), result.getUri());
                expenseAdapter.notifyDataSetChanged();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Toast.makeText(getContext(), "There was an error loading the image, please try again",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateReceiptImage(String expenseId, Uri receiptPhoto)
    {
        ExpenseJob expenseJob = new ExpenseJob((FirestoreJob.JobStatus.IN_PROGRESS));
        MutableLiveData<ExpenseJob> job = new MutableLiveData<>(expenseJob);
        StorageReference receiptPhotoRef =
                storageReference.child("houses/" + houseActivityViewModel.getHouse().getId() + "/receipts/" + expenseId
                        + ".jpg");
        receiptPhotoRef.putFile(receiptPhoto).addOnCompleteListener(task -> {
            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                Uri receiptPhotoUri = task1.getResult();
                LiveData<ExpenseJob> updatePhotoJob =
                        viewModel.updateReceiptImageUri(houseActivityViewModel.getHouse().getId(),
                                expenseId,
                                receiptPhotoUri.toString());
                updatePhotoJob.observe(getViewLifecycleOwner(), ExpenseJob ->
                {
                    if (ExpenseJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
                    {
                        //TODO: toast?
                        currentExpense.set_hasReceipt(true);
                    }
                });
            });
        });
    }

    public static class SettleExpensesDialogFragment extends DialogFragment
    {
        private HouseExpensesFragment houseExpensesFragment;
        Dialog dialog;

        public static SettleExpensesDialogFragment newInstance(HouseExpensesFragment houseExpensesFragment)
        {
            SettleExpensesDialogFragment f = new SettleExpensesDialogFragment();
            f.setHouseExpensesFragment(houseExpensesFragment);
            return f;
        }

        public void setHouseExpensesFragment(HouseExpensesFragment houseExpensesFragment)
        {
            this.houseExpensesFragment = houseExpensesFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            View v = inflater.inflate(R.layout.dialog_yes_no, container, false);
            Button noButton = v.findViewById(R.id.button_no);
            noButton.setOnClickListener(v1 -> dialog.dismiss());
            Button yesButton = v.findViewById(R.id.button_yes);
            yesButton.setOnClickListener(v12 -> {
                houseExpensesFragment.settleExpenses();
                dialog.dismiss();
            });
            TextView settleDialogTextView = v.findViewById(R.id.settle_expense_dialog_text);
            settleDialogTextView.setVisibility(View.VISIBLE);
            return v;
        }


        public void showDialog()
        {
            FragmentManager fm = houseExpensesFragment.getParentFragmentManager();
            this.show(fm, "dialog");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }

    }


    public static class BalanceDialogFragment extends DialogFragment
    {
        private RecyclerView mRecyclerView;
        private BalanceAdapter adapter;
        private HouseExpensesFragment houseExpensesFragment;

        public static BalanceDialogFragment newInstance(HouseExpensesFragment houseExpensesFragment)
        {
            BalanceDialogFragment f = new BalanceDialogFragment();
            f.setHouseExpensesFragment(houseExpensesFragment);
            return f;
        }

        private void setHouseExpensesFragment(HouseExpensesFragment houseExpensesFragment)
        {
            this.houseExpensesFragment = houseExpensesFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View v = inflater.inflate(R.layout.dialog_all_user_balances, container, false);
            mRecyclerView = v.findViewById(R.id.balances_recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            setUpDialog();
            return v;
        }

        private void setUpDialog()
        {
            ArrayList<Pair<String, Double>> namesAndBalances = new ArrayList<>();
            LiveData<GetHouseRoomiesJob> job =
                    HouseRepository.getInstance().getHouseRoomies(houseExpensesFragment.houseActivityViewModel.getHouse().getId());
            job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
                switch (getHouseRoomiesJob.getJobStatus())
                {
                    case SUCCESS:
                        for (User user : getHouseRoomiesJob.getRoomiesList())
                        {
                            String username = user.getUsername();
                            double userBalance = houseExpensesFragment.getBalanceByUid(user.getUid());
                            Pair<String, Double> usernameAndBalance = new Pair<>(username, userBalance);
                            namesAndBalances.add(usernameAndBalance);
                        }
                        adapter = new BalanceAdapter(namesAndBalances);
                        mRecyclerView.setAdapter(adapter);
                    case ERROR:
                        //TODO: implement
                }
            });
        }

        public void showDialog()
        {
            FragmentManager fm = houseExpensesFragment.getParentFragmentManager();
            this.show(fm, "dialog");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }
    }

    public static class AddReceiptDialog extends DialogFragment
    {
        private Button noButton, yesButton;
        private TextView addReceiptDialogTextView;
        private HouseExpensesFragment houseExpensesFragment;

        Dialog dialog;

        public static AddReceiptDialog newInstance(HouseExpensesFragment houseExpensesFragment)
        {
            AddReceiptDialog f = new AddReceiptDialog();
            f.setHouseExpensesFragment(houseExpensesFragment);
            return f;
        }


        public void setHouseExpensesFragment(HouseExpensesFragment houseExpensesFragment)
        {
            this.houseExpensesFragment = houseExpensesFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            View v = inflater.inflate(R.layout.dialog_yes_no, container, false);
            noButton = v.findViewById(R.id.button_no);
            yesButton = v.findViewById(R.id.button_yes);
            noButton.setOnClickListener(v1 -> dialog.dismiss());
            yesButton.setOnClickListener(v12 -> {
                houseExpensesFragment.selectReceiptPicture();
                dialog.dismiss();
            });
            addReceiptDialogTextView = v.findViewById(R.id.settle_expense_dialog_text);
            addReceiptDialogTextView.setVisibility(View.VISIBLE);
            addReceiptDialogTextView.setText(R.string.add_receipt);
            return v;
        }

        public void showDialog()
        {
            FragmentManager fm = houseExpensesFragment.getParentFragmentManager();
            this.show(fm, "dialog");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }
    }

    public static class ReplaceReceiptDialog extends DialogFragment
    {
        private Button replaceReceiptButton;
        private ImageView image;
        private HouseExpensesFragment houseExpensesFragment;
        Dialog dialog;

        public static ReplaceReceiptDialog newInstance(HouseExpensesFragment houseExpensesFragment)
        {
            ReplaceReceiptDialog f = new ReplaceReceiptDialog();
            f.setHouseExpensesFragment(houseExpensesFragment);
            return f;
        }


        public void setHouseExpensesFragment(HouseExpensesFragment houseExpensesFragment)
        {
            this.houseExpensesFragment = houseExpensesFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            View v = inflater.inflate(R.layout.dialog_replace_receipt, container, false);
            replaceReceiptButton = v.findViewById(R.id.replace_receipt_button);
            image = v.findViewById(R.id.current_receipt_image);
            replaceReceiptButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    houseExpensesFragment.selectReceiptPicture();
                }
            });
            Picasso.get().load(houseExpensesFragment.currentExpense.get_receiptImageUriString())
                    .resize(1200, 1600)
                    .centerCrop()
                    .into(image);
            return v;
        }

        public void showDialog()
        {
            FragmentManager fm = houseExpensesFragment.getParentFragmentManager();
            this.show(fm, "dialog");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }
    }

    public static class DeleteExpenseDialog extends DialogFragment
    {
        private HouseExpensesFragment houseExpensesFragment;
        private int position;

        Dialog dialog;

        public static DeleteExpenseDialog newInstance(HouseExpensesFragment houseExpensesFragment)
        {
            DeleteExpenseDialog f = new DeleteExpenseDialog();
            f.setHouseExpensesFragment(houseExpensesFragment);
            return f;
        }

        private void setPosition(int position)
        {
            this.position = position;
        }


        public void setHouseExpensesFragment(HouseExpensesFragment houseExpensesFragment)
        {
            this.houseExpensesFragment = houseExpensesFragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState)
        {
            View v = inflater.inflate(R.layout.dialog_delete_expense, container, false);
            Button noButton = v.findViewById(R.id.button_no);
            Button yesButton = v.findViewById(R.id.button_yes);
            noButton.setOnClickListener(v1 -> dialog.dismiss());
            yesButton.setOnClickListener(v12 -> {
                Expense expense = houseExpensesFragment.expenses.get(position);
                String houseId = houseExpensesFragment.houseActivityViewModel.getHouse().getId();
                houseExpensesFragment.viewModel.deleteExpense(expense, houseId);
                houseExpensesFragment.expenseAdapter.notifyDataSetChanged();
                dialog.dismiss();
            });
            TextView deleteExpenseDialogTextView = v.findViewById(R.id.delete_expense_dialog_text);
            deleteExpenseDialogTextView.setVisibility(View.VISIBLE);
            return v;
        }

        public void showDialog()
        {
            FragmentManager fm = houseExpensesFragment.getParentFragmentManager();
            this.show(fm, "dialog");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            return dialog;
        }
    }
}