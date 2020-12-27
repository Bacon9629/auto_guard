package com.bacon.auto_guard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

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

    private void checkVersion(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("version");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                PackageInfo pkgInfo = null;
                try {
                    pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String myVersion = pkgInfo.versionName;

                String unChange_latest = task.getResult().child("latest").getValue().toString();
                String latest = replace(unChange_latest);

                if (!latest.equals(myVersion)){
                    StringBuilder builder = new StringBuilder();
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);

                    boolean important = task.getResult().child(unChange_latest).child("important").getValue().toString().equals("true");
//                    Log.d("ContentValues",task.getResult().child(unChange_latest).child("important").getValue().toString());

                    builder.append("最新版本 : ").append(latest).append("\t\t\t當前版本 : ").append(myVersion).append("\n\n");
                    for(int i=0;task.getResult().child(unChange_latest).hasChild("description"+i);i++){
                        char a = 248;
                        builder.append(a).append("\t\t");
                        builder.append(task.getResult().child(unChange_latest).child("description"+i).getValue());
                        builder.append("\n");
                    }

                    if (important){
                        builder.append('\n');
                        builder.append("=====此為重大更新、不可以跳過=====").append('\n');
                    }else{
                        alert_builder.setNegativeButton("下次再說",null);
                    }


                    alert_builder.setTitle("檢查更新").setMessage(builder.toString())
                            .setCancelable(false)
                            .setPositiveButton("前往更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Uri uri = Uri.parse(task.getResult()
                                                .child(unChange_latest).child("uri").getValue().toString());

                                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                                        startActivity(intent);

                                        if (important){
                                            checkVersion();
                                        }


                                    } catch (NullPointerException e){
                                        alert_builder.setMessage("取得資料失敗...")
                                                .setNegativeButton("取消",null)
                                                .show();
                                    }
                                }
                            });

                    alert_builder.show();


                }

            }
        });

    }

    private String replace(String str){
        return str.replace("*",".");
    }

}