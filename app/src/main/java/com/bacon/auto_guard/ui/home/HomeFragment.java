package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();

        ArrayList<String> name_data = new ArrayList<>();
        name_data.add("客廳");
        name_data.add("廚房");
        name_data.add("房間1");
        name_data.add("房間2");
        name_data.add("房間3");
        name_data.add("餐廳");
        name_data.add("廁所");


        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recycler_parent = root.findViewById(R.id.recycler_home_parent);
        RecyclerView.Adapter adapter = new Recycler_parent_home(context,name_data);
        recycler_parent.setLayoutManager(new LinearLayoutManager(context));
        recycler_parent.setAdapter(adapter);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }
}