package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bacon.auto_guard.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Recycler_parent_home extends RecyclerView.Adapter<Recycler_parent_home.ViewHolder> {

    Context context;
    ArrayList<String> parent_data;

    public Recycler_parent_home(Context context, ArrayList<String> parent_data){
        this.context = context;
        this.parent_data = parent_data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.recycler_home_parent,parent,false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(parent_data.get(position));
        holder.arrow_button.setOnClickListener(click);

    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,"touch",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public int getItemCount() {
        return parent_data.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, arrow_button;
        RecyclerView son;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.electronic_name);
            arrow_button = itemView.findViewById(R.id.electronic_arrow);
            son = itemView.findViewById(R.id.electronic_recycler_son);
        }
    }
}

