package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bacon.auto_guard.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class Recycler_soon_home extends RecyclerView.Adapter<Recycler_soon_home.ViewHolder> {

    ArrayList<HashMap<String , String>> myData;
    Context context;

    public Recycler_soon_home(Context context, ArrayList<HashMap<String , String>> myData){

    }

    public void notifyMyChanged(ArrayList<HashMap<String , String>> myData){
        super.notifyDataSetChanged();
        this.myData = myData;
    }

    @Override
    public int getItemViewType(int position) {

        if (myData.get(position).get("type").equals("light"))
            return 0;
        else if (myData.get(position).get("type").equals("camera"))
            return 1;
        else
            return 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_home_son_other, parent, false);
            return new ViewHolder_Other(view);
        }
        else if (viewType == 1){
            view = LayoutInflater.from(context).inflate(R.layout.recycler_home_son_camera,parent,false);
            return new ViewHolder_camera(view);
        }

        else{
//            view = null;
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder.returnType() == 0){
            ViewHolder_Other other = (ViewHolder_Other) holder;
            //TODO 這裡寫other的內容

            other.name.setText(myData.get(position).get("name"));

            if (myData.get(position).get("status").equals("ON")){
                other.img_status.setImageDrawable(context.getDrawable(R.drawable.ic_light_on));
//                other.text_status
                other.btn_switch.setImageDrawable(context.getDrawable(R.drawable.ic_on_switch));

            }else{

                other.img_status.setImageDrawable(context.getDrawable(R.drawable.ic_light_off));
//                other.text_status
                other.btn_switch.setImageDrawable(context.getDrawable(R.drawable.ic_off_switch));

            }

            other.btn_switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO 按鈕按下後要做甚麼

                }
            });



            //other end

        }else if (holder.returnType() == 1){
            ViewHolder_camera camera = (ViewHolder_camera) holder;
            //TODO 這裡寫camera的內容


        }



    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        public abstract int returnType();
    }

    public static class ViewHolder_Other extends ViewHolder{
        ImageView img_status;
        ImageButton btn_switch;
        TextView text_status, name;

        public ViewHolder_Other(@NonNull View itemView) {
            super(itemView);
            img_status = itemView.findViewById(R.id.img_status);
            text_status = itemView.findViewById(R.id.text_status);
            btn_switch = itemView.findViewById(R.id.bt_switch);
            name = itemView.findViewById(R.id.son_name);
        }

        @Override
        public int returnType() {
            return 0;
        }
    }

    public static class ViewHolder_camera extends ViewHolder{


        public ViewHolder_camera(@NonNull View itemView) {
            super(itemView);

        }

        @Override
        public int returnType() {
            return 1;
        }
    }

}
