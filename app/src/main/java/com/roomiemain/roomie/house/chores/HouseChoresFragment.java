package com.roomiemain.roomie.house.chores;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.roomiemain.roomie.FirestoreJob;
import com.roomiemain.roomie.MovableFloatingActionButton;
import com.roomiemain.roomie.R;
import com.roomiemain.roomie.User;
import com.roomiemain.roomie.house.HouseActivity;
import com.roomiemain.roomie.house.HouseActivityViewModel;
import com.roomiemain.roomie.house.chores.chore.Chore;
import com.roomiemain.roomie.house.chores.chore.newChoreJob;
import com.roomiemain.roomie.repositories.GetHouseRoomiesJob;
import com.roomiemain.roomie.repositories.HouseRepository;
import com.roomiemain.roomie.util.FirestoreUtil;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.roomiemain.roomie.util.ChoreUtil.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseChoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseChoresFragment extends Fragment implements ChoreAdapter.OnChoreListener, HouseActivity.IOnBackPressed  {


    //recyclerview
    private RecyclerView recyclerView;
    private ChoreAdapter adapter = null;
    private ArrayList<Chore> choreList;
    //view models
    private HouseChoresFragmentViewModel vm;
    private HouseActivityViewModel houseActivityViewModel;
    //navigation
    private MovableFloatingActionButton button;
    private NavController navController;
    private FrameLayout loadingOverlay;
    private boolean firstArrived = false;
    private ArrayList<String> roommatesNamesList;
    private ArrayList<String> rommatesMap;
    //filter and sort data
    private boolean isFiltered = false;
    private boolean isSorted = false;
    private String filterByAssignee = "";
    private String filterByChosenString = "";
    private String filterBySize = "";
    private String sortBy = "";


    public HouseChoresFragment() {
        // Required empty public constructor
    }

    /* create view */
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HouseChoresFragment.
     */
    public static HouseChoresFragment newInstance() {
        return new HouseChoresFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_house_chores, container, false);
        setHasOptionsMenu(true);
        initViewModels();
        initChoreList(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        button = view.findViewById(R.id.fab);
        button.setOnClickListener(view1 -> {
            if(view1 != null){
                Bundle result = new Bundle();
                passFilterAndSortData(result);
                navController.navigate(R.id.action_house_chores_fragment_dest_to_newChoreFragment,result);
            }
        });
        rommatesMap = new ArrayList<>();
        roommatesNamesList = new ArrayList<>();
        loadingOverlay = view.findViewById(R.id.chores_loading_overlay);

        toggleLoadingOverlay(true);
        loadRoommies(view);
    }


    /* ui */
    private void toggleLoadingOverlay(boolean isVisible) {
        if (isVisible) {
            loadingOverlay.setVisibility(View.VISIBLE);
        } else {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void loadRoommies(View view) {
        LiveData<GetHouseRoomiesJob> job = HouseRepository.getInstance().getHouseRoomies(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), getHouseRoomiesJob -> {
            if (getHouseRoomiesJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                for (User user : getHouseRoomiesJob.getRoomiesList()) {
                    roommatesNamesList.add(user.getUsername());
                    rommatesMap.add(user.getProfilePicture());
                }

                if (firstArrived) {
                    toggleLoadingOverlay(false);
                    setRecyclerView(view);
                } else
                    firstArrived = true;
            }
        });
    }

    private void initViewModels() {
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        vm = new ViewModelProvider(this).get(HouseChoresFragmentViewModel.class);
    }

    private void initChoreList(View v) {
        LiveData<AllChoresJob> job;

        //check if the data was filtered before
        if(getArguments().getBoolean("isFiltered",false)) {
            isFiltered = true;
            filterByChosenString = getArguments().getString("filterBy","");
            job = getFilterJob(filterByChosenString,getResources(),getArguments().getString("filter"));
        } else {
            job = vm.getFilteredChores(houseActivityViewModel.getHouse().getId(), FirestoreUtil.CHORE_DONE_FIELD_NAME, "false", null);
        }
        //when get the data, check if it was sorted before
        job.observe(getViewLifecycleOwner(), allChoresJob -> {
            if(allChoresJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                choreList = (ArrayList<Chore>) allChoresJob.getChoreList();
                if(getArguments().getBoolean("isSorted")){
                    sortBy = getArguments().getString("sortBy");
                    isSorted = true;
                    choreList.sort(this::choreComperator);
                }
                if(firstArrived) {
                    toggleLoadingOverlay(false);
                    setRecyclerView(v);
                }
                else
                    firstArrived = true;
            }
        });
    }

    private void setRecyclerView(View v) {
        adapter = new ChoreAdapter(choreList, HouseChoresFragment.this,roommatesNamesList,rommatesMap);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperLeft = getItemTouchHelperLeft();
        itemTouchHelperLeft.attachToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchHelperRight = getItemTouchHelperRight();
        itemTouchHelperRight.attachToRecyclerView(recyclerView);
    }

    /* menu */

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.chore_toolbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.filterOption) {
            showFilterDialog();
            Toast.makeText(getContext(), "filter selected", Toast.LENGTH_LONG).show();
            return true;
        } else if(item.getItemId() == R.id.sortOption){
            showSortDialog();
            Toast.makeText(getContext(), "sort selected", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ItemTouchHelper getItemTouchHelperRight() {
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            private final ColorDrawable background = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.fui_transparent)
            );

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.closeMenu();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };
        return new ItemTouchHelper(touchHelperCallback);
    }

    @NotNull
    private ItemTouchHelper getItemTouchHelperLeft() {
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.fui_transparent)
            );

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.showMenu(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };
        return new ItemTouchHelper(touchHelperCallback);
    }

    /* dialogs */

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.create();
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_sort_chores,null);
        setSortBySpinner(customLayout,alertDialog);
        alertDialog.setView(customLayout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void setSortBySpinner(View customLayout,AlertDialog dialog) {
        PowerSpinnerView spinner = customLayout.findViewById(R.id.sortBySpinner);
        spinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            sortBy = s;
            isSorted = true;
            choreList.sort(this::choreComperator);
            adapter.notifyDataSetChanged();
            dialog.cancel();
        });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.create();
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_filter_by,null);
        setFilterBySpinner(customLayout);
        setDoFilterButton(customLayout, alertDialog);
        alertDialog.setView(customLayout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void setFilterBySpinner(View customLayout) {
        PowerSpinnerView filterBySpinner = customLayout.findViewById(R.id.filterBySpinner);
        filterBySpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i, s) -> {
            filterByChosenString = s;
            if(s.equals(getString(R.string.assignee))) {
                setFilterByAssigneeChosen(customLayout);
            } else if(s.equals(getString(R.string.filterBySizeString))) {
                setFilterBySizeChosen(customLayout);
            } else {
                customLayout.findViewById(R.id.filterByRoomieSpinner).setVisibility(View.GONE);
                customLayout.findViewById(R.id.filterBySizeSpinner).setVisibility(View.GONE);
            }
        });
    }

    private int choreComperator(Chore chore, Chore t1) {
        if(sortBy.equals(getString(R.string.assignee))){
            return chore.get_assignee().compareTo(t1.get_assignee());
        } else if(sortBy.equals(getString(R.string.due_date))) {
            return chore.get_dueDate().compareTo(t1.get_dueDate());
        } else if(sortBy.equals(getString(R.string.choreSize))){
            if(chore.get_score()>t1.get_score()){
                return 0;
            } return -1;
        }
        return 0;
    }

    /* buttons */

    private void setDoFilterButton(View customLayout, AlertDialog dialog) {
        customLayout.findViewById(R.id.doFilterButton).setOnClickListener(view -> {
            LiveData<AllChoresJob> job;
            if(filterByChosenString.equals(getString(R.string.assignee))) {
                job = getFilterJob(filterByChosenString,getResources(),filterByAssignee);
            } else if (filterByChosenString.equals(getString(R.string.filterBySizeString))) {
                job = getFilterJob(filterByChosenString,getResources(),filterBySize);
            } else {
                job = getFilterJob(filterByChosenString,getResources(),"");
            }

            job.observe(getViewLifecycleOwner(), allChoresJob -> {
                if(allChoresJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                    isFiltered = true;
                    choreList = (ArrayList<Chore>) allChoresJob.getChoreList();
                    adapter = new ChoreAdapter(choreList,this,roommatesNamesList,rommatesMap);
                    recyclerView.swapAdapter(adapter,false);
                }
            });
            dialog.cancel();
        });
    }

    @Override
    public void onChoreClick(int pos) {
        Chore chore = choreList.get(pos);
        showChoreFragment(chore);
    }

    @Override
    public void onDeleteClick(int pos) {
        Chore chore = choreList.get(pos);
        adapter.closeMenu();
        LiveData<newChoreJob> job = vm.deleteChoreForever(chore, houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), newChoreJob -> {
            choreList.remove(chore);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onEditClick(int pos) {
        Chore chore = choreList.get(pos);
        showChoreFragment(chore);
    }

    @Override
    public void onMarkAsDoneClick(int pos) {
        Chore chore = choreList.get(pos);
        LiveData<newChoreJob> job = vm.setDone(chore, !chore.is_choreDone(), houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), newChoreJob -> {
            if(newChoreJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                adapter.notifyDataSetChanged();
                adapter.closeMenu();
            }
        });
    }

    /* firestore queries for filter and sort */

    private LiveData<AllChoresJob> getFilterJob(String filterBy, Resources resources, String value){
        if(filterBy.equals(getString(R.string.assignee))) {
            filterByAssignee = value;
            return vm.getFilteredChores(houseActivityViewModel.getHouse().getId(),
                                        FirestoreUtil.ASSIGNEE_FIELD_NAME, value,null);
        } else if(filterBy.equals(getString(R.string.filterByDoneString))){
            //filter by done
            return vm.getFilteredChores(houseActivityViewModel.getHouse().getId(),
                                        FirestoreUtil.CHORE_DONE_FIELD_NAME, "true",null);
        } else if (filterBy.equals(getString(R.string.filterByUndoneString))) {
            //filter by undone
            return vm.getFilteredChores(houseActivityViewModel.getHouse().getId(),
                                        FirestoreUtil.CHORE_DONE_FIELD_NAME, "false",null);
        }
        else if (filterBy.equals(getString(R.string.filterByLastWeekString))) {
            // filter by last week
            return vm.getFilteredChores(houseActivityViewModel.getHouse().getId(),
                                        FirestoreUtil.CREATION_DATE_FIELD_NAME, "week", null);
        }
        else if (filterBy.equals(getString(R.string.filterByLastMonthString))) {
            return vm.getFilteredChores(houseActivityViewModel.getHouse().getId(),
                                        FirestoreUtil.CREATION_DATE_FIELD_NAME, "month", null);
        }
        else if(filterBy.equals(getString(R.string.filterBySizeString))) {
            filterBySize = value;
            return vm.getFilteredChores(houseActivityViewModel.getHouse().getId(),
                                        FirestoreUtil.SIZE_FIELD_NAME, value, resources);
        }

        return vm.getAllChores(houseActivityViewModel.getHouse().getId());

    }

    private void setFilterBySizeChosen(View customLayout) {
        customLayout.findViewById(R.id.filterByRoomieSpinner).setVisibility(View.GONE);
        PowerSpinnerView filterBySizeSpinner = customLayout.findViewById(R.id.filterBySizeSpinner);
        filterBySizeSpinner.setVisibility(View.VISIBLE);
        filterBySizeSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i12, s12) -> {
            filterBySize = s12;
        });
    }

    private void setFilterByAssigneeChosen(View customLayout) {
        customLayout.findViewById(R.id.filterBySizeSpinner).setVisibility(View.GONE);
        PowerSpinnerView filterByRoomieSpinner = customLayout.findViewById(R.id.filterByRoomieSpinner);
        filterByRoomieSpinner.setItems(roommatesNamesList);
        filterByRoomieSpinner.setVisibility(View.VISIBLE);
        filterByRoomieSpinner.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (i1, s1) -> {
            filterByAssignee = s1;
        });
    }

    /* navigation */

    public void showChoreFragment(Chore chore) {
        Bundle result = new Bundle();
        passChoreData(chore, result);
        navController.navigate(R.id.action_house_chores_fragment_dest_to_choreFragment, result);
    }

    private void passChoreData(Chore chore, Bundle result) {
        result.putString(CHORE_TITLE,chore.get_title());
        result.putString(CHORE_ID,chore.get_id());
        result.putString(CHORE_ASSIGNEE,chore.get_assignee());
        result.putStringArrayList(ROOMMIES_NAMES,roommatesNamesList);
        passFilterAndSortData(result);
        DateFormat df = new SimpleDateFormat(DATE_PATTERN);
        result.putString(CHORE_DUE_DATE, df.format(chore.get_dueDate()));
        if(chore.get_description() == null){
            result.putString(CHORE_CONTENT,"");
        }else
            result.putString(CHORE_CONTENT,chore.get_description());
    }

    private void passFilterAndSortData(Bundle result) {
        result.putBoolean(IS_FILTERED, isFiltered);
        result.putString(FILTER_BY, filterByChosenString);
        if(filterByChosenString.equals(getString(R.string.assignee))) {
            result.putString(FILTER, filterByAssignee);
        } else if(filterByChosenString.equals(getString(R.string.filterBySizeString))) {
            result.putString(FILTER, filterBySize);
        } else {
            result.putString(FILTER, "");
        }
        result.putBoolean(IS_SORTED, isSorted);
        result.putString(SORT_BY,sortBy);
    }

    @Override
    public boolean onBackPressed() {
        if (adapter.isMenuShown()) {
            adapter.closeMenu();
            return true;
        } else {
            return false;
        }
    }
}