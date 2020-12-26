package com.bacon.auto_guard.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bacon.auto_guard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

class Recycler_son_home extends RecyclerView.Adapter<Recycler_son_home.ViewHolder> {

    ArrayList<Son_Data_format> myData;
    Context context;
    SharedPreferences preferences;
    Home_Data home_data;
    String parent;
    String touch;

    public Recycler_son_home(Context context, ArrayList<Son_Data_format> myData,String parent) {
        this.context = context;
        this.myData = myData;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
//        Log.d(TAG,"inin");
        home_data = new Home_Data();
        this.parent = parent;
        touch = preferences.getString("home_electronic", "");
    }


    public void notifyMyChanged(ArrayList<Son_Data_format> myData) {
        this.myData = myData;

        super.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        if (Objects.equals(myData.get(position).getType(), "default"))
            return 0;
        else if (Objects.equals(myData.get(position).getType(), "camera"))
            return 1;
        else
            return 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        Log.d(TAG,"inin");

        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_home_son_other, parent, false);
            return new ViewHolder_Other(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.recycler_home_son_camera, parent, false);
            return new ViewHolder_camera(view);
        } else {
//            view = null;
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder.returnType() == 0) {
            ViewHolder_Other other = (ViewHolder_Other) holder;
            //TODO 這裡寫other的內容

//            Log.d(TAG,myData.get(position).getParent());

            other.name.setText(myData.get(position).getName());

//            Log.d(TAG,position+" => "+myData.get(position).get("name"));

            if (myData.get(position).getStatus().equals("ON")) {
                other.img_status.setImageDrawable(context.getDrawable(R.drawable.ic_light_on));
//                other.text_status
//                other.btn_switch.setImageDrawable(context.getDrawable(R.drawable.ic_on_switch));
                other.btn_switch.setBackground(context.getDrawable(R.drawable.background_light_shap));

            } else {

                other.img_status.setImageDrawable(context.getDrawable(R.drawable.ic_light_off));
//                other.text_status
//                other.btn_switch.setImageDrawable(context.getDrawable(R.drawable.ic_off_switch));
                other.btn_switch.setBackground(context.getDrawable(R.drawable.background_dark_shap));

            }



            other.btn_switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO 按鈕按下後要做甚麼
//                    Log.d(TAG,"position = "+position+" content = "+myData.get(position).getParent()+myData.get(position).getName());
                    home_data.send_switch_internet_data(context, myData.get(position));


                }
            });


            //other end

        } else if (holder.returnType() == 1) {
            ViewHolder_camera camera = (ViewHolder_camera) holder;
            //TODO 這裡寫camera的內容


        }


    }

    @Override
    public int getItemCount() {

//        Log.d(TAG,"data =  "+myData.get(0).getParent()+" , "+a);
        if (myData.size() == 0){
            return 0;
        }
        if (!touch.equals(parent)){
            return 0;
        }

        return myData.size();
    }

    public abstract static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract int returnType();
    }

    public static class ViewHolder_Other extends ViewHolder {
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

    public static class ViewHolder_camera extends ViewHolder {


        public ViewHolder_camera(@NonNull View itemView) {
            super(itemView);

        }

        @Override
        public int returnType() {
            return 1;
        }
    }

}
