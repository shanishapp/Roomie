package com.example.roomie.house.chores;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.roomie.FirestoreJob;
import com.example.roomie.MovableFloatingActionButton;
import com.example.roomie.R;
import com.example.roomie.house.HouseActivityViewModel;
import com.example.roomie.house.chores.chore.Chore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HouseChoresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HouseChoresFragment extends Fragment implements ChoreAdapter.OnChoreListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter = null;
    private HouseActivityViewModel houseActivityViewModel;
    private HouseChoresFragmentViewModel vm;
    private ArrayList<Chore> choreList;
    private MovableFloatingActionButton button;
    private NavController navController;

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
        houseActivityViewModel = new ViewModelProvider(requireActivity()).get(HouseActivityViewModel.class);
        vm = new ViewModelProvider(this).get(HouseChoresFragmentViewModel.class);
        LiveData<allChoresJob> job  = vm.getAllChores(houseActivityViewModel.getHouse().getId());
        job.observe(getViewLifecycleOwner(), allChoresJob -> {
            if(allChoresJob.getJobStatus() == FirestoreJob.JobStatus.SUCCESS) {
                choreList = (ArrayList<Chore>) allChoresJob.getChoreList();
                adapter = new ChoreAdapter(choreList,HouseChoresFragment.this);
                RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
        return v;
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
    }


    @Override
    public void onChoreClick(int pos) {
        Chore chore = choreList.get(pos);
        showChoreFragment(chore);
    }

    private void showChoreFragment(Chore chore) {
        Bundle result = new Bundle();
        result.putString("choreTitle",chore.get_title());
        result.putString("choreId",chore.get_id());
        result.putString("choreAssignee",chore.get_assignee());
        String pattern = "dd/MM/yyyy HH:mm";
        DateFormat df = new SimpleDateFormat(pattern);
        result.putString("choreDueDate",df.format(chore.get_dueDate()).toString());
        navController.navigate(R.id.action_house_chores_fragment_dest_to_choreFragment, result);
    }
}