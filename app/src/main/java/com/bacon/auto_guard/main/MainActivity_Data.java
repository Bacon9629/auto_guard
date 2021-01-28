package com.bacon.auto_guard.main;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bacon.auto_guard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

class MainActivity_Data {

    FirebaseFirestore db;
    CollectionReference img_db;
    Context context;

    ArrayList<DocumentSnapshot> list_date_snapshot = new ArrayList<>();
    Map<String, Object> map_admin = new HashMap<>();
    Map<String, Object> map_custom = new HashMap<>();



    public MainActivity_Data(Context context){
        this.context = context;
        db = FirebaseFirestore.getInstance();
        img_db = db.collection("user1").document("robot")
                .collection("image");
    }

    public void download(String title, Runnable do_next){
        switch (title){
            case "管理人清單":{
                img_db.document("admin_list").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                map_admin = task.getResult().getData();
                                do_next.run();
                            }
                        });
                break;
            }
            case "管理訪客清單":{
                img_db.document("custom_list").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                map_custom = task.getResult().getData();
                                do_next.run();
                            }
                        });
                break;
            }
            case "觀看截圖清單":{
                img_db.document("snapshot").collection("date").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                list_date_snapshot = new ArrayList<>(task.getResult().getDocuments());
                                do_next.run();
                            }
                        });
                break;
            }
            default:
        }

    }

    public void delete(String title, String name){
        switch (title){
            case "管理人清單":{
                img_db.document("admin_list").update(name, FieldValue.delete())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "刪除資料失敗", Toast.LENGTH_SHORT).show();
                                delete(title, name);
                            }
                        });
                break;
            }
            case "管理訪客清單":{
                img_db.document("custom_list").update(name, FieldValue.delete())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "刪除資料失敗", Toast.LENGTH_SHORT).show();
                                delete(title, name);
                            }
                        });
                break;
            }
        }
    }

    public void delete(String title, String date, String time){
        String date2 = date.replace("/", ":");
        if (title.equals("觀看截圖清單")){
            img_db.document("snapshot").collection("date").document(date2)
                    .update(time, FieldValue.delete())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "刪除資料失敗", Toast.LENGTH_SHORT).show();
                            delete(title, date, time);
                        }
                    });

        }
    }

    public HashMap<String, Map<String, Object>> get_snapshot(){
        HashMap<String, Map<String, Object>> result = new HashMap<>();
        for (DocumentSnapshot temp : list_date_snapshot){
            result.put(temp.getId(), temp.getData());
        }
        return result;
    }

    public ArrayList<String> get_snapshot_date(){
        return new ArrayList<>(get_snapshot().keySet());
    }

    public Map<String, Object> get_map_admin(){
        return map_admin;
    }

    public Map<String , Object> get_map_custom(){
        return map_custom;
    }

}
