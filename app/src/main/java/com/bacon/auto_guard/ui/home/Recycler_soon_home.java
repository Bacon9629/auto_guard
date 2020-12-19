package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class Recycler_soon_home extends RecyclerView.Adapter<Recycler_soon_home.ViewHolder> {

    public Recycler_soon_home(Context context, HashMap<String,Object> data){

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
