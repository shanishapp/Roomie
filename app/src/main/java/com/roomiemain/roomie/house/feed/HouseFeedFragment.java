package com.roomiemain.roomie.house.feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.User;
import com.roomiemain.roomie.house.HouseActivityViewModel;
import com.roomiemain.roomie.house.chores.AllChoresJob;
import com.roomiemain.roomie.house.chores.HouseChoresFragmentViewModel;
import com.roomiemain.roomie.house.chores.chore.Chore;
import com.roomiemain.roomie.house.expenses.AllExpensesJob;
import com.roomiemain.roomie.house.expenses.Expense;
import com.roomiemain.roomie.house.expenses.HouseExpensesViewModel;
import com.roomiemain.roomie.house.groceries.AllGroceriesJob;
import com.roomiemain.roomie.house.groceries.HouseGroceriesFragmentViewModel;
import com.roomiemain.roomie.house.groceries.grocery.Grocery;
import com.roomiemain.roomie.repositories.GetHouseRoomiesJob;
import com.roomiemain.roomie.repositories.HouseRepository;
import com.roomiemain.roomie.util.FirestoreUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseFeedFragment extends Fragment  {

    private static final int CHORE_FEED_TYPE = 0;
    private static final int EXPENSE_FEED_TYPE = 1;
    private HouseActivityViewModel houseActivityViewModel;

    private HouseChoresFragmentViewModel houseChoresFragmentViewModel;

    private HouseExpensesViewModel houseExpensesViewModel;

    private HouseGroceriesFragmentViewModel houseGroceriesFragmentViewModel;

    private TextView totalHouseSpendingTextView;

    private TextView totalGroceriesLeftTextView;

    private TextView topContributorTextView;
    ArrayList<Feed> feedArrayList;


    private FeedAdapter adapter;
    private ArrayList<String> roommatesNamesList;
    private ArrayList<String> rommatesMap;
    private RecyclerView recyclerView;

    public HouseFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseFeedFragment.
     */
    public static HouseFeedFragment newInstance() {
        return new HouseFeedFragment();
    }

    /* create view */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        houseChoresFragmentViewModel = new ViewModelProvider(requireActivity()).get(HouseChoresFragmentViewModel.class);
        houseExpensesViewModel = new ViewModelProvider(requireActivity()).get(HouseExpensesViewModel.class);
        houseGroceriesFragmentViewModel = new ViewModelProvider(requireActivity()).get(HouseGroceriesFragmentViewModel.class);

        setUIElements(view);
        loadRoommies(view);
        loadStatsExpenses(view);
        loadStatsGroceries(view);
        loadStatsScore(view);
    }

    /* ui */
    private void setUIElements(View rootView) {
        totalHouseSpendingTextView = rootView.findViewById(R.id.totalHouseSpendingTextView);
        totalGroceriesLeftTextView = rootView.findViewById(R.id.totalGroceriesLeftTextView);
        topContributorTextView = rootView.findViewById(R.id.topContributorTextView);
        roommatesNamesList = new ArrayList<>();
        rommatesMap = new ArrayList<>();
        feedArrayList = new ArrayList<>();

    }

    private void setRecyclerView(View v, List<Feed> feedList) {
        adapter = new FeedAdapter(feedList,roommatesNamesList,rommatesMap);
        recyclerView = v.findViewById(R.id.feed_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    /* data */
    private void loadRoommies(View view) {
        LiveData<GetHouseRoomiesJob> job = HouseRepository.getInstance().getHouseRoomies(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            if (getHouseRoomiesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                for (User user : getHouseRoomiesJob.getRoomiesList()) {
                    roommatesNamesList.add(user.getUsername());
                    rommatesMap.add(user.getProfilePicture());
                }
                loadLastChores(view);
            }
        });
    }

    private void loadLastChores(View view) {
        LiveData<AllChoresJob> job = houseChoresFragmentViewModel.getFilteredChores(houseActivityViewModel.getHouse().getId(), FirestoreUtil.CREATION_DATE_FIELD_NAME, "week", getResources());
        job.observe(getViewLifecycleOwner(), allChoresJob -> {
            if(allChoresJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                for (Chore chore : allChoresJob.getChoreList()) {
                    Feed feed = new Feed(CHORE_FEED_TYPE, chore.get_assignee(), getString(R.string.new_chore)+chore.get_title()+"\n"+chore.get_description(), chore.get_creationDate());
                    feedArrayList.add(feed);
                }
                loadLastExpenses(view);
            }
        });

    }

    private void loadLastExpenses(View view) {
        LiveData<AllExpensesJob> job = houseExpensesViewModel.getExpensesFromLastWeek(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if (allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS){
                for(Expense expense: allExpensesJob.getExpenses()){
                    Feed feed = new Feed(EXPENSE_FEED_TYPE,expense.get_payerName(),getString(R.string.new_expense)+expense.get_cost(),expense.get_creationDate());
                    feedArrayList.add(feed);
                }
                loadLastGroceries(view);
            }
        });

    }

    private void loadLastGroceries(View view) {
        LiveData<AllGroceriesJob> job = houseGroceriesFragmentViewModel.getAllGroceriesFromLastWeek(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allGroceriesJob -> {
            if (allGroceriesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS){
                for(Grocery grocery: allGroceriesJob.getGroceryList()){
                    Feed feed = new Feed(EXPENSE_FEED_TYPE,grocery.get_creatorName(),getString(R.string.new_grocery)+grocery.get_name(),grocery.get_creationDate());
                    feedArrayList.add(feed);
                }
                feedArrayList.sort((feed, feed1) -> feed1.getDateCreated().compareTo(feed.getDateCreated()));
                setRecyclerView(view, feedArrayList);
            }
        });
    }

    /* statistics data*/
    private void loadStatsScore(View view) {
        LiveData<AllChoresJob> job = houseChoresFragmentViewModel.getLastMonthAchievements(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), new Observer<AllChoresJob>() {
            @Override
            public void onChanged(AllChoresJob allChoresJob) {
                if(allChoresJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS){
                    HashMap<String, Integer> scores = new HashMap<>();
                    for(Chore chore: allChoresJob.getChoreList()){
                        if(!scores.containsKey(chore.get_assignee())){
                            scores.put(chore.get_assignee(),chore.get_score());
                        }else{
                            scores.put(chore.get_assignee(),scores.get(chore.get_assignee())+chore.get_score());
                        }
                    }
                    String winner = "-";
                    if(!scores.isEmpty()){
                        winner = scores.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                    }
                    ((TextView)view.findViewById(R.id.topContributorTextView)).setText(winner);
                }


            }
        });
    }

    private void loadStatsGroceries(View view) {
        LiveData<AllGroceriesJob> job = houseGroceriesFragmentViewModel.getAllGroceries(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allGroceriesJob -> {
            if(allGroceriesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS)
            ((TextView) view.findViewById(R.id.totalGroceriesLeftTextView)).setText(String.valueOf(allGroceriesJob.getGroceryList().size()));
        });
    }

    private void loadStatsExpenses(View view) {
        LiveData<AllExpensesJob> job = houseExpensesViewModel.getUnSettledExpenses(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allExpensesJob -> {
            if(allExpensesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                int totalExpenses = 0;
                for(Expense expense: allExpensesJob.getExpenses()){
                    totalExpenses += expense.get_cost();
                }
                ((TextView)view.findViewById(R.id.totalHouseSpendingTextView)).setText(String.valueOf(totalExpenses));

            }
        });
    }

}