package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Home_Data home_data = new Home_Data();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("載入中...");
        progressDialog.setCancelable(false);

        homeViewModel =
                new ViewModelProvider(HomeFragment.this).get(HomeViewModel.class);
        homeViewModel.putContext(context);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //TODO 這裡要從網路上抓parent name的資料
                ArrayList<String> name_data = new ArrayList<>();
                name_data.add("房間 1");
                name_data.add("房間 2");
//        name_data.add("房間 3");

                RecyclerView recycler_parent = root.findViewById(R.id.recycler_home_parent);
                Recycler_parent_home parent_adapter = new Recycler_parent_home(context,home_data.getParent_list());
                recycler_parent.setLayoutManager(new LinearLayoutManager(context));
                recycler_parent.setAdapter(parent_adapter);

                progressDialog.dismiss();

                homeViewModel.getSonData().observe(getViewLifecycleOwner(), (Observer<ArrayList<Son_Data_format>>) s -> {
                    parent_adapter.putSon_data(s);
//            Log.d(TAG, Objects.requireNonNull(s.get(0).get("name")));
                });
            }
        };


        home_data.download_parent_data(handler,runnable);


        return root;
    }
}