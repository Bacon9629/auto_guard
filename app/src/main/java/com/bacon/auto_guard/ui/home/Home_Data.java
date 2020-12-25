package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import androidx.annotation.NonNull;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

class Home_Data {

    ArrayList<Son_Data_format> son_data = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference internet_db;

    public Home_Data(){
        internet_db = database.getReference("home").child("electronic");
    }

    public void get_internet_data(ValueEventListener valueEventListener){

        internet_db.addValueEventListener(valueEventListener);

    }

    public ArrayList<Son_Data_format> select_parent_getData(DataSnapshot snapshot, String parent){
        son_data.clear();
//        Log.d(TAG,snapshot.child(parent).getChildren().iterator().next().getValue().toString());
        for (DataSnapshot dataSnapshot : snapshot.child(parent).getChildren()) {
            son_data.add(
                    new Son_Data_format("default", parent, dataSnapshot.getKey(), dataSnapshot.getValue().toString()));
//            Log.d(TAG,dataSnapshot.getKey());
        }
        return son_data;
    }

    public ArrayList<Son_Data_format> getSon_data(){
        //從getSharePreference來決定要抓取甚麼資料

        return son_data;
    }
    public void send_switch_internet_data(Context context, Son_Data_format son_data){

        //在這裡才改變資料

        String temp;

        if (son_data.getStatus().equals("ON"))
            temp = "OFF";
        else
            temp = "ON";


        Log.d(TAG,son_data.getParent()+son_data.getName());
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
