package com.bacon.auto_guard.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bacon.auto_guard.R;
import com.google.firebase.firestore.Blob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

class Recycler_menu_face_list extends RecyclerView.Adapter<Recycler_menu_face_list.viewHolder> {
    Map<String, Object> person_data;
    ArrayList<String> person_key;
    HashMap<String, Map<String, Object>> snapshot_data;
    ArrayList<String> date_key;
    ArrayList<String> the_date_img_key;  //已經選定哪一天了，裡面的資料是snapshot_img的time
    Context context;
    String title;
    boolean is_parent;
    String parent;
    MainActivity_Data db;

    public Recycler_menu_face_list(ArrayList<String> date_key, boolean is_parent, String parent
            , HashMap<String, Map<String, Object>> snapshot_data, String title, Context context
            , MainActivity_Data db){
        this.date_key = date_key;
        this.snapshot_data = snapshot_data;
        this.title = title;
        this.context = context;
        this.parent = parent;
        this.is_parent = is_parent;
        this.db = db;
        this.person_data = null;
    }

    public Recycler_menu_face_list(Map<String, Object> person_data, String title, Context context
            , MainActivity_Data db){
        this.context = context;
        this.person_data = person_data;
        this.title = title;
        this.date_key = null;
        this.snapshot_data = null;
        this.db = db;
        person_key = new ArrayList<>(person_data.keySet());
        Collections.sort(person_key);
    }

    public void change_parent(boolean is_parent, String  parent){
        this.is_parent = is_parent;
        this.parent = parent;
        if (!is_parent) {
            this.the_date_img_key = new ArrayList<>(snapshot_data.get(parent).keySet());
            Collections.sort(this.the_date_img_key);
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
//                holder.textView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        change_parent(false, v.getTag().toString());
//                    }
//                });
                holder.image.setVisibility(View.GONE);
                holder.outside.setTag(date_key.get(position));
                holder.outside.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_parent(false, v.getTag().toString());
                    }
                });
                holder.outside.setOnLongClickListener(null);

            }else{

                if (position == 0){
                    holder.image.setVisibility(View.GONE);
                    holder.textView.setText("上一頁");
//                    holder.textView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            change_parent(true, "");
//                        }
//                    });
                    holder.outside.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            change_parent(true, "");
                        }
                    });
                    holder.outside.setOnLongClickListener(null);
                }else {
                    int b_position = position - 1;
                    Blob blob = (Blob) snapshot_data.get(parent).get(the_date_img_key.get(b_position));
                    Bitmap bitmap = convertbytesToIcon(blob.toBytes());
                    holder.image.setImageBitmap(bitmap);

                    holder.textView.setText(the_date_img_key.get(b_position));

                    holder.textView.setOnClickListener(null);
                    holder.outside.setOnClickListener(null);
                    holder.outside.setTag(parent + "'" + the_date_img_key.get(b_position));
                    holder.outside.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("你要對他幹嘛?")
                                    .setPositiveButton("刪掉他", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String[] list = v.getTag().toString().split("'");
                                            db.delete("觀看截圖清單", list[0], list[1]);
                                            snapshot_data.get(list[0]).remove(list[1]);
                                            the_date_img_key.remove(list[1]);

                                            notifyDataSetChanged();

                                        }
                                    })
                                    .setNegativeButton("沒事", null)
                                    .show();
                            return false;
                        }
                    });


                }
            }


        }else if ("管理人清單".equals(title)){

            Blob blob = (Blob) person_data.get(person_key.get(position));
            holder.image.setImageBitmap(convertbytesToIcon(blob.toBytes()));

            holder.textView.setText(person_key.get(position));

            holder.outside.setTag(person_key.get(position));
            holder.outside.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("你要對他幹嘛?")
                            .setPositiveButton("刪掉他", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    db.delete("管理人清單", v.getTag().toString());
                                    person_data.remove(v.getTag().toString());
                                    person_key.remove(v.getTag().toString());
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("沒事", null).show();
                    return false;
                }
            });

        }else if ("管理訪客清單".equals(title)){
            Map map = (Map) person_data.get(person_key.get(position));

            Log.d(TAG, person_key.get(position));

            Blob blob = (Blob) map.get("image");
            holder.image.setImageBitmap(convertbytesToIcon(blob.toBytes()));

            String string = person_key.get(position) + "\n到期日 : " + map.get("until");
            holder.textView.setText(string);

            holder.outside.setTag(person_key.get(position));
            holder.outside.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("你要對他幹嘛?")
                            .setPositiveButton("刪掉他", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    db.delete("管理訪客清單", v.getTag().toString());
                                    person_data.remove(v.getTag().toString());
                                    person_key.remove(v.getTag().toString());
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("沒事", null).show();

                    return false;
                }
            });


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
            return person_data.size();
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
