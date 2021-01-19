package com.bacon.auto_guard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVersion();

        inputName();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_robot, R.id.navigation_home, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    private void inputName() {
        SharedPreferences preferences = getSharedPreferences("activity", 0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (!preferences.getString("name", "-1").equals("-1"))
            return;

        View v = LayoutInflater.from(context).inflate(R.layout.input_name, null, false);
        EditText editText = v.findViewById(R.id.editTextTextPersonName);
        new AlertDialog.Builder(context).setTitle("輸入我的名子").setView(v).setCancelable(false)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {

                            db.collection("user1").document("user")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String name = editText.getEditableText().toString();

                                    if (name.equals("") || name.equals(" ")) {
                                        Toast.makeText(context, "輸入錯誤", Toast.LENGTH_SHORT).show();
                                        inputName();
                                        return;
                                    }

                                    if (Objects.requireNonNull(task.getResult()).contains(name)) {

                                        //名子跟網路資料庫重複

                                        new AlertDialog.Builder(context).setTitle("你是 " + name + " 嗎?")
                                                .setMessage("這個名子已經登記過了?\n若是同時有兩個同樣名稱的裝置在線上，可能會遭成不可回的錯誤!!")
                                                .setPositiveButton("我就是要用", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        preferences.edit().putString("name", name).apply();
                                                        Toast.makeText(context, name + " 你好!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .setNegativeButton("重新輸入", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        inputName();
                                                    }
                                                })
                                                .setCancelable(false).show();
//                                    Toast.makeText(context,"這個名子已經重複過了，請輸入其他名稱!!!",Toast.LENGTH_LONG).show();
//                                    inputName();
                                    } else {

                                        //名子OK，上傳ING

                                        try {
                                            Map<String, Object> temp = new HashMap<>();
                                            temp.put(name, "name");
                                            db.collection("user1").document("user").update(temp)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(context, name + " 你好!", Toast.LENGTH_SHORT).show();
                                                            preferences.edit().putString("name", name).apply();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(context, "網路有點問題，請確認網路狀態", Toast.LENGTH_LONG).show();
                                                            inputName();
                                                        }
                                                    });

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, "網路有問題，請確認網路狀態", Toast.LENGTH_LONG).show();
                                            inputName();
                                        }

                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(context, "網路連線錯誤", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();

    }

    private HashMap<String, Object> get_version_content(QuerySnapshot task, String version){
        List<DocumentSnapshot> lists = task.getDocuments();
        HashMap<String, Object> maps = new HashMap<>();

        for(DocumentSnapshot item : lists){
            if(item.getId().equals(version)){
                maps.put("important", item.get("important"));
                maps.put("uri", item.get("uri"));

                for(int i=0;i<30;i++){
                    if (item.contains("description" + i)){
                        maps.put("description" + i, item.get("description" + i));
                    }else{
                        break;
                    }
                }
                break;
            }
        }

        return maps;

    }

    private String get_latest_version(QuerySnapshot task){
        List<DocumentSnapshot> lists = task.getDocuments();
        for(DocumentSnapshot item : lists){
            if(item.getId().equals("latest")){
                return item.get("latest").toString();
            }
        }
        return null;
    }

    private void checkVersion() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("version").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> taskk) {
                        PackageInfo pkgInfo = null;

                        String latest = get_latest_version(taskk.getResult());
                        HashMap<String, Object> version_content = get_version_content(taskk.getResult(), latest);

                        try {
                            pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String myVersion = pkgInfo.versionName;

                        if (!latest.equals(myVersion)) {
                            StringBuilder builder = new StringBuilder();
                            AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);

                            boolean important = version_content.get("important").toString().equals("true");
//                    Log.d("ContentValues",task.getResult().child(unChange_latest).child("important").getValue().toString());

                            builder.append("最新版本 : ").append(latest).append("\t\t\t當前版本 : ").append(myVersion).append("\n\n");
                            for (int i = 0; version_content.containsKey("description"+i); i++) {
                                char a = 248;
                                builder.append(a).append("\t\t");
                                builder.append(version_content.get("description" + i).toString());
                                builder.append("\n");
                            }

                            if (important) {
                                builder.append('\n');
                                builder.append("=====此為重大更新、不可以跳過=====").append('\n');
                            } else {
                                alert_builder.setNegativeButton("下次再說", null);
                            }


                            alert_builder.setTitle("檢查更新").setMessage(builder.toString())
                                    .setCancelable(false)
                                    .setPositiveButton("前往更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                Uri uri = Uri.parse(version_content.get("uri").toString());

                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);

                                                if (important) {
                                                    checkVersion();
                                                }


                                            } catch (NullPointerException e) {
                                                alert_builder.setMessage("取得資料失敗...")
                                                        .setNegativeButton("取消", null)
                                                        .show();
                                            }
                                        }
                                    });

                            alert_builder.show();


                        }
                    }
                });

    }

}