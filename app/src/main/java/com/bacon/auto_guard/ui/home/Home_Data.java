package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bacon.auto_guard.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.LogRecord;

import androidx.annotation.NonNull;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

class Home_Data {

    ArrayList<Son_Data_format> son_data = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference internet_db;
    ArrayList<String > parent_list;

    public Home_Data(){
        internet_db = database.getReference("home").child("electronic");
    }

    public void download_parent_data(Context context,Handler handler, Runnable next_step){
        //只有呼叫下載而已，下載完成之後呼叫runnable，必須要在runnable裡面執行getParent_list

        parent_list = new ArrayList<>();

        internet_db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                try {
                    for (DataSnapshot snapshot : Objects.requireNonNull(task.getResult()).getChildren()) {

                        parent_list.add(snapshot.getKey());

                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"下載資料失敗，請檢察網路有無開啟",Toast.LENGTH_SHORT).show();
                    download_parent_data(context,handler,next_step);
                }
//                parent_list.add("pass");
//                parent_list.add("pass");
                // 這裡回傳資料
                handler.post(next_step);

            }
        });

    }

    public ArrayList<String> getParent_list(){
        //return parent list
        return parent_list;
    }

    public void get_internet_data(@NonNull ValueEventListener valueEventListener){
        //資料更新時，及時取得資料
        internet_db.addValueEventListener(valueEventListener);

    }

    public ArrayList<Son_Data_format> select_parent_getData(Context context, DataSnapshot snapshot, String parent){
        //資料整理，回傳相對應parent的DATA
        son_data.clear();
//        Log.d(TAG,snapshot.child(parent).getChildren().iterator().next().getValue().toString());
        try {
            for (DataSnapshot dataSnapshot : snapshot.child(parent).getChildren()) {
                son_data.add(
                        new Son_Data_format("default", parent, dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
//            Log.d(TAG,dataSnapshot.getKey());
            }
        }catch (NullPointerException e){
           e.printStackTrace();
           Toast.makeText(context,"取得資料錯誤，請重新開啟程式",Toast.LENGTH_LONG).show();
        }
        return son_data;
    }

    public void send_switch_internet_data(Context context, Son_Data_format son_data){

        //設定資料到database

        //在這裡才改變資料

        String temp;

        if (son_data.getStatus().equals("ON"))
            temp = "OFF";
        else
            temp = "ON";


//        Log.d(TAG,son_data.getParent()+son_data.getName());
        internet_db.child(son_data.getParent()).child(son_data.getName())
                .setValue(temp)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"傳送訊息失敗",Toast.LENGTH_LONG).show();
                    }
                });

    }

}
