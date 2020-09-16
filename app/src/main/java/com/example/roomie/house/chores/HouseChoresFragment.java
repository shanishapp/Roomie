package com.example.roomie.house.chores;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
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
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.example.roomie.FirestoreJob;
import com.example.roomie.MovableFloatingActionButton;
import com.example.roomie.R;
import com.example.roomie.User;
import com.example.roomie.house.HouseActivity;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.house.chores.chore.Chore;
import com.example.roomie.house.chores.chore.newChoreJob;
import com.example.roomie.repositories.GetHouseRoomiesJob;
import com.example.roomie.repositories.HouseRepository;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseChoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseChoresFragment extends Fragment implements ChoreAdapter.OnChoreListener, HouseActivity.IOnBackPressed  {

    private RecyclerView recyclerView;
    private ChoreAdapter adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private HouseChoresFragmentViewModel vm;
    private ArrayList<Chore> choreList;
    private MovableFloatingActionButton button;
    private NavController navController;
    private FrameLayout loadingOverlay;
    private boolean firstArrived = false;
    private ArrayList<String> roommatesNamesList;
    private ArrayList<String> rommatesMap;


    public HouseChoresFragment() {
        // Required empty public constructor
    }

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
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_house_chores, container, false);
        //init variables
        setHasOptionsMenu(true);
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        vm = new ViewModelProvider(this).get(HouseChoresFragmentViewModel.class);
        LiveData<allChoresJob> job  = vm.getAllChores(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allChoresJob -> {
            if(allChoresJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                choreList = (ArrayList<Chore>) allChoresJob.getChoreList();
                if(firstArrived) {
                    toggleLoadingOverlay(false);
                    setRecyclerView(v);
                }
                else
                    firstArrived = true;
            }
        });
        return v;
    }

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
            Toast.makeText(getContext(), "sort selected", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.create();
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_filter_by,null);
        alertDialog.setView(customLayout);

        // add a button

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // create and show
        // the alert dialog

        alertDialog.show();
    }

    private void setRecyclerView(View v) {
        adapter = new ChoreAdapter(choreList, HouseChoresFragment.this,roommatesNamesList,rommatesMap);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperLeft = getItemTouchHelperLeft();
        itemTouchHelperLeft.attachToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchHelperRight = getItemTouchHelperRight();
        itemTouchHelperRight.attachToRecyclerView(recyclerView);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        button = view.findViewById(R.id.fab);
        //set up add button
        button.setOnClickListener(view1 -> {
            if(view1 != null){
                navController.navigate(R.id.action_house_chores_fragment_dest_to_newChoreFragment);
            }
        });
        rommatesMap = new ArrayList<>();
        roommatesNamesList = new ArrayList<>();
        loadingOverlay = view.findViewById(R.id.chores_loading_overlay);

        toggleLoadingOverlay(true);
        loadRoommies(view);
    }

    @Override
    public void onChoreClick(int pos) {
        Chore chore = choreList.get(pos);
        showChoreFragment(chore);
    }

    @Override
    public void onDeleteClick(int pos) {
        Chore chore = choreList.get(pos);
        Toast.makeText(getContext(),chore.get_title()+" is deleted",Toast.LENGTH_LONG).show();
        adapter.closeMenu();
        vm.deleteChoreForever(chore,houseActivityViewModel.getHouse().getId());
        adapter.notifyDataSetChanged();
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

    public void showChoreFragment(Chore chore) {
        Bundle result = new Bundle();
        result.putString("choreTitle",chore.get_title());
        result.putString("choreId",chore.get_id());
        result.putString("choreAssignee",chore.get_assignee());
        result.putStringArrayList("roommiesNames",roommatesNamesList);
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        result.putString("choreDueDate", df.format(chore.get_dueDate()));
        navController.navigate(R.id.action_house_chores_fragment_dest_to_choreFragment, result);
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
}