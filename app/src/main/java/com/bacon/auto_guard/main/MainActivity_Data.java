package com.bacon.auto_guard.main;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.PrecomputedText;
import android.util.Log;
import android.widget.Toast;

import com.bacon.auto_guard.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import javax.xml.transform.Result;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

class MainActivity_Data {

    private final FirebaseFirestore db;
    private final CollectionReference img_db;
    private final Context context;

    private ArrayList<DocumentSnapshot> list_date_snapshot = new ArrayList<>();
    private Map<String, Object> map_admin = new HashMap<>();
    private Map<String, Object> map_custom = new HashMap<>();
    private final StorageReference storage;


    public MainActivity_Data(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        img_db = db.collection("user1").document("robot")
                .collection("image");
        storage = FirebaseStorage.getInstance().getReference().child("image");
    }

    public void download(String title, Runnable do_next) {
        OnFailureListener failureListener = e -> Toast.makeText(context, "下載失敗，正在重新執行", Toast.LENGTH_LONG).show();

        switch (title) {
            case "管理人清單": {
                img_db.document("admin_list").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                map_admin = task.getResult().getData();
                                do_next.run();

                            }
                        })
                        .addOnFailureListener(failureListener);
                break;
            }
            case "管理訪客清單": {
                img_db.document("custom_list").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                map_custom = task.getResult().getData();
                                do_next.run();
                            }
                        })
                        .addOnFailureListener(failureListener);
                break;
            }
            case "觀看截圖清單": {
                img_db.document("snapshot").collection("date").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                list_date_snapshot = new ArrayList<>(task.getResult().getDocuments());
                                do_next.run();
                            }
                        })
                        .addOnFailureListener(failureListener);
                break;
            }
            default:
        }

    }


    public void delete(String title, String name) {
        OnFailureListener failureListener = e -> {
            Toast.makeText(context, "刪除出問題 ! ", Toast.LENGTH_SHORT).show();
            delete(title, name);
        };
        switch (title) {
            case "管理人清單": {
                Log.d(TAG, name);
                storage.child(("admin_image/" + name + ".png")).delete().addOnFailureListener(failureListener);
                img_db.document("admin_list").update(name, FieldValue.delete())
                        .addOnFailureListener(failureListener);
                break;
            }
            case "管理訪客清單": {
                storage.child(("custom_image/" + name + ".png")).delete().addOnFailureListener(failureListener);
                img_db.document("custom_list").update(name, FieldValue.delete())
                        .addOnFailureListener(failureListener);
                break;
            }
        }
    }

    public void delete(String title, String date, String time) {
        String date2 = date.replace("/", ":");
        if (title.equals("觀看截圖清單")) {
            img_db.document("snapshot").collection("date").document(date2)
                    .update(time, FieldValue.delete())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "刪除資料失敗，正在重新執行", Toast.LENGTH_SHORT).show();
                            delete(title, date, time);
                        }
                    });

        }
    }

    public HashMap<String, Map<String, Object>> get_snapshot() {
        HashMap<String, Map<String, Object>> result = new HashMap<>();
        for (DocumentSnapshot temp : list_date_snapshot) {
            result.put(temp.getId(), temp.getData());
        }
        return result;
    }

    public ArrayList<String> get_snapshot_date() {
        return new ArrayList<>(get_snapshot().keySet());
    }

    public Map<String, Object> get_map_admin() {
        return map_admin;
    }

    public Map<String, Object> get_map_custom() {
        return map_custom;
    }

    public void upload(String title, String name, byte[] image, Runnable do_next) {
        switch (title) {
            case "管理人清單": {
                StorageReference db_path = storage.child(("admin_image/" + name + ".png"));
                UploadTask uploadTask = db_path.putBytes(image);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return db_path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            HashMap<String , Object> map = new HashMap<>();
                            map.put("image", downloadUri.toString());
                            map.put("pass", "待測");

                            img_db.document("admin_list").update(name, map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            do_next.run();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "上傳失敗，正在重新執行", Toast.LENGTH_SHORT).show();
                                            upload(title, name, image, do_next);
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "上傳失敗，正在重新執行", Toast.LENGTH_SHORT).show();
                            upload(title, name, image, do_next);
                        }
                    }
                });

                break;
            }
            case "管理訪客清單": {
                StorageReference db_path = storage.child(("custom_image/" + name + ".png"));
                UploadTask uploadTask = db_path.putBytes(image);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return db_path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("until", getDateTime());
                            map.put("image", downloadUri.toString());
                            map.put("pass", "待測");

                            img_db.document("custom_list").update(name, map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            do_next.run();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "上傳失敗，正在重新執行", Toast.LENGTH_SHORT).show();
                                            upload(title, name, image, do_next);
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "上傳失敗，正在重新執行", Toast.LENGTH_SHORT).show();
                            upload(title, name, image, do_next);
                        }
                    }
                });
                break;
            }

        }
    }

    public String getDateTime() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return year + "/" + month + "/" + day;
    }
}

