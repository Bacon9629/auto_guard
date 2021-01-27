package com.bacon.auto_guard.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bacon.auto_guard.R;
import com.google.firebase.firestore.Blob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

class Recycler_menu_face_list extends RecyclerView.Adapter<Recycler_menu_face_list.viewHolder> {
    Map<String, Object> data;
    HashMap<String, Map<String, Object>> snapshot_data;
    ArrayList<String> date_key;
    ArrayList<String> the_date_img_key;
    Context context;
    String title;
    boolean is_parent;
    String parent;

    public Recycler_menu_face_list(ArrayList<String> date_key, boolean is_parent, String parent
            , HashMap<String, Map<String, Object>> snapshot_data, String title, Context context){
        this.date_key = date_key;
        this.snapshot_data = snapshot_data;
        this.title = title;
        this.context = context;
        this.parent = parent;
        this.is_parent = is_parent;
        this.data = null;
    }

    public Recycler_menu_face_list(Map<String, Object> data, String title, Context context){
        this.context = context;
        this.data = data;
        this.title = title;
        this.date_key = null;
        this.snapshot_data = null;
    }

    public void change_parent(boolean is_parent, String  parent){
        this.is_parent = is_parent;
        this.parent = parent;
        if (!is_parent) {
            this.the_date_img_key = new ArrayList<>(snapshot_data.get(parent).keySet());
        }
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_menu_face, parent, false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.image.setVisibility(View.VISIBLE);

        if (title.equals("觀看截圖清單")){

            if (is_parent){

                holder.textView.setText(date_key.get(position).replace(":", "/"));
                holder.textView.setTag(date_key.get(position));
                holder.image.setVisibility(View.GONE);
                holder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_parent(false, v.getTag().toString());
                    }
                });

            }else{

                if (position == 0){
                    holder.image.setVisibility(View.GONE);
                    holder.textView.setText("上一頁");
                    holder.textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            change_parent(true, "");
                        }
                    });
                }else {
                    int b_position = position - 1;
                    Blob blob = (Blob) snapshot_data.get(parent).get(the_date_img_key.get(b_position));
                    Bitmap bitmap = convertbytesToIcon(blob.toBytes());
                    holder.image.setImageBitmap(bitmap);

                    holder.textView.setText(the_date_img_key.get(b_position));
                }
            }


        }
    }

    public Bitmap convertbytesToIcon(byte[] output) {
        try {
            return BitmapFactory.decodeByteArray(output, 0,
                    output.length);
        } catch (Exception e) {
            Log.d(TAG, "bitmap wrong");
            return null;
        }
    }


    @Override
    public int getItemCount() {
        if (title.equals("觀看截圖清單")){
            if (is_parent){
                return date_key.size();
            }else{
                return snapshot_data.get(parent).size() + 1;
            }
        }else {
            return data.size();
        }
    }

    class viewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView textView;
        TextView outside;

        public viewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.menu_face_list_recycler_image);
            textView = v.findViewById(R.id.menu_face_list_recycler_text);
            outside = v.findViewById(R.id.menu_face_list_recycler_outside);
        }
    }

}
