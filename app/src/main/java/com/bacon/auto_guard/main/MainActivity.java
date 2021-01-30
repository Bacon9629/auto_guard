package com.bacon.auto_guard.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bacon.auto_guard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_GALLERY = 7;
    private static final int PICK_FROM_CAMERA = 9;
    private static final int PICK_FROM_GET = 5;
    private final Context context = this;
    private final String TAG = "ContentValues";
    private ImageView build_image;
    private Bitmap temp_bitmap = null;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
                                                .setMessage("這個名子已經登記過了?\n若是同時有兩個同樣名稱的裝置在線上，可能會遭成不可挽回的錯誤!!")
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
                                            checkVersion();
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

    public void face_list(MenuItem item) {
        String title = item.getTitle().toString();
        set_menu_AlertDialog(title);
    }

    private void set_menu_AlertDialog(String title){



        MainActivity_Data mainActivity_data = new MainActivity_Data(context);

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        @SuppressLint("InflateParams") View v = LayoutInflater.from(context).inflate(R.layout.menu_face_list, null);
        dialog.setView(v);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));

        TextView title_text = v.findViewById(R.id.menu_face_list_title);
        RecyclerView list_view = v.findViewById(R.id.menu_face_list_recycler);
        TextView close_button = v.findViewById(R.id.menu_face_list_close);
        TextView check_button = v.findViewById(R.id.menu_face_list_check);
        TextView build_button = v.findViewById(R.id.menu_face_list_build);

        switch (title){
            case "管理人清單":{

                mainActivity_data.download("管理人清單", new Runnable() {
                    @Override
                    public void run() {
                        Recycler_menu_face_list adapter = new Recycler_menu_face_list(
                                mainActivity_data.get_map_admin(), "管理人清單", context, mainActivity_data);
                        list_view.setLayoutManager(new LinearLayoutManager(context));
                        list_view.setAdapter(adapter);
                    }
                });

                break;
            }
            case "管理訪客清單":{
                mainActivity_data.download("管理訪客清單", new Runnable() {
                    @Override
                    public void run() {
                        Recycler_menu_face_list adapter = new Recycler_menu_face_list(
                                mainActivity_data.get_map_custom(), "管理訪客清單", context, mainActivity_data);
                        list_view.setLayoutManager(new LinearLayoutManager(context));
                        list_view.setAdapter(adapter);
                    }
                });

                break;
            }
            case "觀看截圖清單":{
                build_button.setVisibility(View.GONE);

                mainActivity_data.download("觀看截圖清單", new Runnable() {
                    @Override
                    public void run() {
                        Recycler_menu_face_list adapter = new Recycler_menu_face_list(mainActivity_data.get_snapshot_date()
                                , true, "", mainActivity_data.get_snapshot()
                                , "觀看截圖清單", context, mainActivity_data);

                        list_view.setLayoutManager(new LinearLayoutManager(context));
                        list_view.setAdapter(adapter);


                    }
                });



                break;
            }
            default:
        }

        title_text.setText(title);

        build_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder build_builder = new AlertDialog.Builder(context);
                AlertDialog build_dialog = build_builder.create();

                Window window1 = build_dialog.getWindow();
                window1.setBackgroundDrawable(new ColorDrawable(0));

                View v2 = LayoutInflater.from(context).inflate(R.layout.menu_face_list_build, null);
                build_image = v2.findViewById(R.id.menu_face_list_build_image);
                EditText build_edit = v2.findViewById(R.id.menu_face_list_build_edit);
                TextView build_title = v2.findViewById(R.id.menu_face_list_build_title);
                ImageButton build_check_button = v2.findViewById(R.id.menu_face_list_build_check_button);

                build_title.setText((title + " - 建立"));

                build_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String type = "file";
                        if (!check_permission(type))
                            return;

                        chose_image(type);


//                        new AlertDialog.Builder(context).setTitle("從哪裡取得照片?")
//                                .setNegativeButton("從檔案", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String type = "file";
//                                        if (!check_permission(type))
//                                            return;
//
//                                        chose_image(type);
//
//                                    }
//                                })
//                                .setPositiveButton("從相機", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String type = "camera";
//                                        if (!check_permission(type))
//                                            return;
//
//                                        chose_image(type);
//
//                                    }
//                                })
//                                .show();
                    }
                });

                build_dialog.setView(v2);
                build_dialog.show();

                build_check_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte[] bytes = bitmapToByte(temp_bitmap);
                        String edit_name = build_edit.getEditableText().toString();

                        if (edit_name == null || temp_bitmap == null){
                            Toast.makeText(context, "圖片內容或姓名沒打!!!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("上傳照片中...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        mainActivity_data.upload(title, edit_name, bytes, new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                set_menu_AlertDialog(title);
                                progressDialog.dismiss();
                            }
                        });
                        build_dialog.dismiss();
                    }
                });

            }
        });

        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private boolean check_permission(String type){

        switch (type){

            case "camera":{
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);

                }

                return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
            }

            case "file":{

                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);

                }

                return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;

            }

            default:
                return true;

        }




    }

    private void chose_image(String type) {

        switch (type){
            case "file":{
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
                break;
            }

            case "camera":{
//                ContentValues value = new ContentValues();
//                value.put(MediaStore.Audio.Media.MIME_TYPE, "image/jpeg");
//                Uri uri= getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                        value);
//                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());

//                Intent intent = new Intent(); //呼叫照相機
//                intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
//
//                startActivityForResult(intent, PICK_FROM_CAMERA);

                break;
            }


        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_CAMERA || requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                ContentResolver cr = this.getContentResolver();

                Bitmap bitmap = null;
                temp_bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    bitmap = resizeBitMap(bitmap);
                    build_image.setImageBitmap(bitmap);
                    temp_bitmap = bitmap;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError ee){
                    try {
                        BitmapFactory.Options mOptions = new BitmapFactory.Options();
                        //Size=2為將原始圖片縮小1/2，Size=4為1/4，以此類推
                        mOptions.inSampleSize = 3;
                        bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri),null,mOptions);
                        build_image.setImageBitmap(bitmap);
                        temp_bitmap = bitmap;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }



            }
        }
    }

    public Bitmap resizeBitMap(Bitmap bitmap){
        Matrix matrix = new Matrix();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

//想要的大小
        int newWidth = 360;
        int newHeight = 520;

//計算比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

// 設定 Matrix 物件，設定 x,y 向的縮放比例
        matrix.postScale(scaleWidth, scaleHeight);

/*
 * creatBitmp的參數依序如下：
 * 原圖資源
 * 第一個pixel點的x座標
 * 第一個pixel點的y座標
 * 圖片的總pixel行
 * 圖片的總pixel列
 * Matrix 的設定
 * 是否filter圖片
 */
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
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

    public byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        return bStream.toByteArray();
    }

}