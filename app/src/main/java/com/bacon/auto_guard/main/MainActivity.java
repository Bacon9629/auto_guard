package com.bacon.auto_guard.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.parseIntent;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_GALLERY = 7;
    private static final int PICK_FROM_CAMERA = 9;
    private static final int PICK_FROM_GET = 5;
    private final Context context = this;
    private static final String TAG = "ContentValues";
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

    private HashMap<String, Object> get_version_content(QuerySnapshot task, String version) {
        List<DocumentSnapshot> lists = task.getDocuments();
        HashMap<String, Object> maps = new HashMap<>();

        for (DocumentSnapshot item : lists) {
            if (item.getId().equals(version)) {
                maps.put("important", item.get("important"));
                maps.put("uri", item.get("uri"));

                for (int i = 0; i < 30; i++) {
                    if (item.contains("description" + i)) {
                        maps.put("description" + i, item.get("description" + i));
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        return maps;

    }

    private HashMap<String, String> get_latest_version(QuerySnapshot task) {
        List<DocumentSnapshot> lists = task.getDocuments();
        HashMap<String, String> map = new HashMap<>();
        for (DocumentSnapshot item : lists) {
            if (item.getId().equals("latest")) {
                map.put("latest", item.get("latest").toString());
                map.put("force", item.get("force").toString());

                return map;
            }
        }
        return map;
    }

    private boolean important_version(String device, String cloud) {
        String[] device_list = device.split("\\.");
        String[] cloud_list = cloud.split("\\.");

        for (int i = 0; i < 3; i++) {
            if (cloud_list[i].length() > device_list[i].length())
                return true;
            else if (cloud_list[i].length() < device_list[i].length())
                return false;
        }

        for (int i = 0; i < 3; i++) {
            int cloud_int = Integer.parseInt(cloud_list[i]);
            int device_int = Integer.parseInt(device_list[i]);
            if (cloud_int > device_int)
                return true;
            else if (cloud_int < device_int)
                return false;
        }


        return false;
    }

    private void checkVersion() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("version").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> taskk) {
                        PackageInfo pkgInfo = null;

                        HashMap<String, String> latest = get_latest_version(taskk.getResult());
                        HashMap<String, Object> version_content = get_version_content(taskk.getResult(), latest.get("latest"));

                        try {
                            pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "取得手機當前版本失敗", Toast.LENGTH_SHORT).show();
                            checkVersion();
                            return;
                        }
                        String myVersion = pkgInfo.versionName;

                        delete_apkFile(myVersion);

                        if (!Objects.equals(latest.get("latest"), myVersion)) {
                            StringBuilder builder = new StringBuilder();
                            AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);

                            boolean important = important_version(myVersion, latest.get("force"));

                            builder.append("最新版本 : ").append(latest.get("latest")).append("\t\t\t當前版本 : ").append(myVersion).append("\n\n");
                            for (int i = 0; version_content.containsKey("description" + i); i++) {
                                char a = 248;
                                builder.append(a).append("\t\t");
                                builder.append(version_content.get("description" + i).toString());
                                builder.append("\n");
                            }

                            if (important) {
                                builder.append('\n');
                                builder.append("\n重大更新版本 : ").append(latest.get("force")).append("\n");
                                builder.append("\n=====你的版本低於重大更新版本=====\n");
                            } else {
                                alert_builder.setNegativeButton("下次再說", null);
                            }


                            alert_builder.setTitle("檢查更新").setMessage(builder.toString())
                                    .setCancelable(false)
                                    .setPositiveButton("前往更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                            checkVersion();
                                            try {

                                                request_permission("file");
                                                if (!permissionCheck("file")) {
                                                    Toast.makeText(context, "快點允許權限", Toast.LENGTH_LONG).show();
                                                    checkVersion();
                                                    return;
                                                }

                                                if (check_apk_is_exist(latest.get("latest"))){
                                                    checkVersion();
                                                    String apkName = "/"+latest.get("latest")+".apk";
                                                    Toast.makeText(context, "檔案已存在，去選擇安裝", Toast.LENGTH_LONG).show();
                                                    planeB(context, context.getExternalFilesDir("/newAPK/").getAbsolutePath() + apkName);
                                                }else{
                                                    ProgressDialog progressDialog = new ProgressDialog(context);
                                                    progressDialog.setTitle("正在下載檔案...");
                                                    progressDialog.setCancelable(false);
                                                    progressDialog.show();

                                                    Toast.makeText(context, "本機內找不到檔案，正在從網路上下載", Toast.LENGTH_LONG).show();
                                                    upData(version_content.get("uri").toString(), latest.get("latest"));
                                                }

                                            } catch (NullPointerException e) {
                                                alert_builder.setMessage("取得更新資料失敗...")
                                                        .setNegativeButton("取消", null)
                                                        .show();
                                                checkVersion();
                                            }
                                        }
                                    });

                            alert_builder.show();


                        }
                    }
                });

    }

    private boolean check_apk_is_exist(String version) {
        String apkName = "/"+version+".apk";
        String PATH = context.getExternalFilesDir("/newAPK/").getAbsolutePath() + apkName;
        File file = new File(PATH);
        return file.exists();
    }
    private void delete_apkFile(String version){
        String apkName = "/"+version+".apk";
        String PATH = context.getExternalFilesDir("/newAPK/").getAbsolutePath() + apkName;
        File file = new File(PATH);
        if (file.exists()){
            file.delete();
        }

    }

    public void face_list(MenuItem item) {
        String title = item.getTitle().toString();
        set_menu_AlertDialog(title);
    }

    private void set_menu_AlertDialog(String title) {


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

        switch (title) {
            case "管理人清單": {

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
            case "管理訪客清單": {
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
            case "觀看截圖清單": {
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
                        request_permission(type);
                        if (!permissionCheck(type)) {
                            Toast.makeText(context, "請允許權限", Toast.LENGTH_LONG).show();
                            return;
                        }
                        chose_image(type);
                    }
                });

                build_dialog.setView(v2);
                build_dialog.show();

                build_check_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String edit_name = build_edit.getEditableText().toString();

                        if (edit_name.equals("") || temp_bitmap == null) {
                            Toast.makeText(context, "圖片內容或姓名沒打!!!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        byte[] bytes = bitmapToByte(temp_bitmap);

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


    private boolean permissionCheck(String type) {

        switch (type) {

            case "camera": {
                return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            }

            case "file": {
                return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
            case "install": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED;
                }else {
                    return true;
                }

            }

            default:
                return true;

        }

    }

    private void request_permission(String type) {
        switch (type) {

            case "camera": {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);

                }
            }

            case "file": {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

                }
            }
            case "install": {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 3);
                    }

                }

            }
        }
    }

    private void chose_image(String type) {

        switch (type) {
            case "file": {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
                break;
            }

            case "camera": {
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

                temp_bitmap = null;
                try {
                    temp_bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    build_image.setImageBitmap(temp_bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError ee) {
                    try {
                        BitmapFactory.Options mOptions = new BitmapFactory.Options();
                        //Size=2為將原始圖片縮小1/2，Size=4為1/4，以此類推
                        mOptions.inSampleSize = 3;
                        temp_bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, mOptions);
                        build_image.setImageBitmap(temp_bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        return bStream.toByteArray();
    }

    public void upData(String url, String apkName) {
        Intent updataService = new Intent(context, UpdateService.class);
        updataService.putExtra("downloadurl", url);
        updataService.putExtra("apkName", apkName);
        startService(updataService);
    }

    private void planeB(Context context, String PATH) {

        File file = new File(PATH);
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent();
//            intent.setAction("android.intent.action.INSTALL_PACKAGE");
        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
//            intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(intent);
    }
}