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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.LogRecord;

import androidx.annotation.NonNull;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

class Home_Data {

    ArrayList<Son_Data_format> son_data = new ArrayList<>();
    FirebaseFirestore db;
    ArrayList<String > parent_list;

    public Home_Data(){
        db = FirebaseFirestore.getInstance();
    }

    public void download_parent_data(Context context,Handler handler, Runnable next_step){
        //只有呼叫下載而已，下載完成之後呼叫runnable，必須要在runnable裡面執行getParent_list

        parent_list = new ArrayList<>();

        db.collection("user1").document("home").collection("electronic").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> taskk) {
                        try {
                            for (DocumentSnapshot snapshot : Objects.requireNonNull(taskk.getResult()).getDocuments()) {
                                parent_list.add(snapshot.getId());
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

    public void get_internet_data(@NonNull EventListener<QuerySnapshot> EventListener){
        //資料更新時，及時取得資料
//        internet_db.addValueEventListener(EventListener);

        db.collection("user1").document("home").collection("electronic")
                .addSnapshotListener(EventListener);

    }

    public ArrayList<Son_Data_format> select_parent_getData(Context context, QuerySnapshot snapshot, String parent){
        //資料整理，回傳相對應parent的DATA
        son_data.clear();



        Map<String, Object> map = new HashMap<>();
        for(DocumentSnapshot item : snapshot.getDocuments()){
            if (item.getId().equals(parent)){
                map = item.getData();
//                Log.d(TAG, map.size()+"");
            }
        }


        if (map == null)
            return son_data;

        ArrayList<String> keys = new ArrayList<>(map.keySet());

        try{
            for (String key : keys){
                son_data.add(
                        new Son_Data_format("default", parent, key, map.get(key).toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"取得資料錯誤",Toast.LENGTH_LONG).show();
            son_data = select_parent_getData(context, snapshot, parent);
        }

//        Log.d(TAG, snapshot.toString());
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

        db.collection("user1").document("home").collection("electronic")
                .document(son_data.getParent()).update(son_data.getName(), temp)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"傳送訊息失敗",Toast.LENGTH_LONG).show();
                        send_switch_internet_data(context, son_data);
                    }
                });

    }

}
