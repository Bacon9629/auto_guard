package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bacon.auto_guard.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Recycler_parent_home extends RecyclerView.Adapter<Recycler_parent_home.ViewHolder> {

    Context context;
    ArrayList<String> parent_data;
    TextView last_view;
    RecyclerView last_recycler;


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

        holder.arrow_button.setTag(parent_data.get(position));
        holder.arrow_button.setOnClickListener(setClick(holder.arrow_button, holder.recycler_son));

        holder.name.setText(parent_data.get(position));
        holder.name.setOnClickListener(setClick(holder.arrow_button, holder.recycler_son));



    }

    private View.OnClickListener setClick(TextView now_text, RecyclerView now_reccler){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,now_text.getTag().toString(),Toast.LENGTH_SHORT).show();

                if (last_view != null){
                    if (last_view == now_text){

                        last_view = null;
                        now_text.setBackground(context.getDrawable(R.drawable.ic_arrow_down));

                        //TODO 關閉last_view or now_text下面的recyclerView，Adapter不要換，換裡面的data就好


                    }else{

                        now_text.setBackground(context.getDrawable(R.drawable.ic_arrow_up));
                        last_view.setBackground(context.getDrawable(R.drawable.ic_arrow_down));
                        last_view = now_text;

                        //TODO 關閉last_view下面的recyclerView，開啟now_text下面的recycler_View，Adapter不要換，換裡面的data就好

                    }
                }else{
                    last_view = now_text;
                    now_text.setBackground(context.getDrawable(R.drawable.ic_arrow_up));
                    //TODO 開啟last_view or now_text下面的recyclerView，Adapter不要換，換裡面的data就好
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

