package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bacon.auto_guard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Context context;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        Log.d(TAG,"big");

        context = getActivity();

        //TODO 這裡要從網路上抓parent name的資料
        ArrayList<String> name_data = new ArrayList<>();
        name_data.add("房間 1");
        name_data.add("房間 2");
        name_data.add("房間 3");

        Home_Data home_data = new Home_Data();

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.putContext(context);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recycler_parent = root.findViewById(R.id.recycler_home_parent);
        Recycler_parent_home parent_adapter = new Recycler_parent_home(context,name_data);
        recycler_parent.setLayoutManager(new LinearLayoutManager(context));
        recycler_parent.setAdapter(parent_adapter);

        homeViewModel.getSonData().observe(getViewLifecycleOwner(), (Observer<ArrayList<Son_Data_format>>) s -> {
            parent_adapter.putSon_data(s);
//            Log.d(TAG, Objects.requireNonNull(s.get(0).get("name")));
        });

        return root;
    }
}