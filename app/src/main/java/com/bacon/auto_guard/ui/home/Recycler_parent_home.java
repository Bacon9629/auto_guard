package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bacon.auto_guard.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class Recycler_parent_home extends RecyclerView.Adapter<Recycler_parent_home.ViewHolder> {

    Context context;
    ArrayList<String> parent_data;
    TextView last_text;
    RecyclerView last_recycler;
    Recycler_son_home son_adapter;
    ArrayList<Son_Data_format> son_data;
    SharedPreferences preferences;

    public Recycler_parent_home(Context context, ArrayList<String> parent_data){
        this.context = context;
        this.parent_data = parent_data;
        this.son_data = new ArrayList<>();
        this.preferences = context.getSharedPreferences(context.getString(R.string.preference_name),0);
        son_adapter = new Recycler_son_home(context,son_data);
    }

    public void putSon_data(ArrayList<Son_Data_format> son_data){
        this.son_data = son_data;
        son_adapter.notifyMyChanged(son_data);
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.recycler_home_parent,parent,false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.arrow_button.setTag(parent_data.get(position));
        holder.arrow_button.setOnClickListener(setClick(holder.arrow_button, holder.recycler_son));

        holder.name.setText(parent_data.get(position));
        holder.name.setOnClickListener(setClick(holder.arrow_button, holder.recycler_son));

        holder.recycler_son.setLayoutManager(new LinearLayoutManager(context));
        holder.recycler_son.setAdapter(son_adapter);



    }

    private View.OnClickListener setClick(TextView now_text, RecyclerView now_recyler){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,now_text.getTag().toString(),Toast.LENGTH_SHORT).show();

                if (last_text != null){
                    if (last_text == now_text){

                        last_text = null;
                        now_text.setBackground(context.getDrawable(R.drawable.ic_arrow_down));
                        last_recycler = null;

                        //TODO 關閉now_text下面的recyclerView，Adapter不要換，換裡面的data就好

                        preferences.edit().putString("home_electronic","null").apply();

                        now_recyler.setVisibility(View.GONE);

                        Log.d(TAG,"touch1");

                    }else{

                        now_text.setBackground(context.getDrawable(R.drawable.ic_arrow_up));
                        last_text.setBackground(context.getDrawable(R.drawable.ic_arrow_down));

                        //TODO 關閉last_text下面的recyclerView，開啟now_text下面的recycler_View，Adapter不要換，換裡面的data就好


                        last_recycler.setVisibility(View.GONE);
                        now_recyler.setVisibility(View.VISIBLE);

                        preferences.edit().putString("home_electronic",now_text.getTag().toString()).apply();
                        Log.d(TAG,"touch2" + now_text.getTag().toString());

//                        now_recyler.setAdapter(son_adapter);

                        //end

                        last_text = now_text;
                        last_recycler = now_recyler;

                    }
                }else{
                    last_text = now_text;
                    last_recycler = now_recyler;
                    now_text.setBackground(context.getDrawable(R.drawable.ic_arrow_up));
                    //TODO 開啟last_text or now_text下面的recyclerView，Adapter不要換，換裡面的data就好
                    now_recyler.setVisibility(View.VISIBLE);
                    preferences.edit().putString("home_electronic",now_text.getTag().toString()).apply();
//                    now_recyler.setAdapter(son_adapter);
                    Log.d(TAG,"touch3");
                }

            }
        };

    }



    @Override
    public int getItemCount() {
        return parent_data.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, arrow_button;
        RecyclerView recycler_son;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.electronic_name);
            arrow_button = itemView.findViewById(R.id.electronic_arrow);
            recycler_son = itemView.findViewById(R.id.electronic_recycler_son);
        }
    }
}

