package com.bacon.auto_guard.ui.robot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bacon.auto_guard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RobotFragment extends Fragment {

    private RobotViewModel robotViewModel;
//    private DatabaseReference db;
    private FirebaseFirestore db;
    private Context context;
    private Robot_Data robot_data;
    private String name;
    private X_Y_Convert convert;
    private final String temp = "";
    private boolean flag_touching = false;
    private boolean flag_no_one_use = true;
    private String internet_now_use = "";
    private final float[] control_spot_origin_position = new float[2];
    private int direction = 0;
    private int speed = 0;

    private ConstraintLayout control_layout;
    private ToggleButton hand_switch;
    private ImageView touch_spot;
    private TextView control_name;
    private ImageView realtime_img;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        robotViewModel =
                new ViewModelProvider(this).get(RobotViewModel.class);
        View root = inflater.inflate(R.layout.fragment_robot, container, false);

        context = getActivity();
        db = FirebaseFirestore.getInstance();
        convert = new X_Y_Convert();
        robot_data = new Robot_Data(context);

        control_layout = root.findViewById(R.id.control_arrow_layout);
        touch_spot = root.findViewById(R.id.control_touch_spot);
        control_name = root.findViewById(R.id.control_name);
        name = context.getSharedPreferences("activity",0).getString("name","無名氏");
        realtime_img = root.findViewById(R.id.realtime_video);
        control_name.setText(name);

        realtime_img_video();


        control_spot_origin_position[0] = (float) 195;
        control_spot_origin_position[1] = (float) 170;

        set_control_keyboard(control_layout);

        update_thread();

        hand_switch = root.findViewById(R.id.control_hand_switch);

        sethand_switch(hand_switch);
//        ensure_AutoSwitch_status(hand_switch);

        return root;
    }

    private void realtime_img_video() {

        db.collection("user1").document("robot_image")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().getBlob("realtime_image") != null){
                    Bitmap bitmap = convertbytesToIcon(
                            task.getResult().getBlob("realtime_image").toBytes());
                    realtime_img.setImageBitmap(bitmap);
                }else{
                    realtime_img.setImageResource(R.drawable.ic_floor_robot);
                }

            }
        });

        db.collection("user1").document("robot_image")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(value.getBlob("realtime_image") != null){
                            Bitmap bitmap = convertbytesToIcon(
                                    value.getBlob("realtime_image").toBytes());
                            realtime_img.setImageBitmap(bitmap);
                        }else{
                            realtime_img.setImageResource(R.drawable.ic_floor_robot);
                        }


                    }
                });

    }

    public Bitmap convertbytesToIcon(byte[] output) {
// OutputStream out;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(output, 0,
                    output.length);
            return bitmap;
        } catch (Exception e) {
            Log.d(TAG, "bitmap wrong");
            return null;
        }
    }

    private void set_control_keyboard(ConstraintLayout control_layout) {
        control_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            upload_speed_direction();
                        case MotionEvent.ACTION_MOVE:
                            flag_touching = true;

                            int x = (int)event.getX();
                            int y = (int)event.getY();
                            direction = convert.convert(x,y);
                            speed = convert.get_speed(x,y);
//                            db.collection("user1").document("robot")
//                                    .update("direction", convert.convert(x,y));
//                            db.collection("user1").document("robot")
//                                    .update("speed", convert.get_speed(x,y));
                            set_touch_spot(event.getX(),event.getY());
                            break;

                        case MotionEvent.ACTION_UP:
                            flag_touching = false;
                            direction = 0;
                            speed = 0;
                            set_touch_spot(control_spot_origin_position[0]+50,control_spot_origin_position[1]+57);

                            db.collection("user1").document("robot")
                                    .update("speed", 0)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(context,"上傳錯誤",Toast.LENGTH_LONG).show();
                                            control_layout.callOnClick();
                                        }
                                    });

                            db.collection("user1").document("robot")
                                    .update("direction", 0)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(context,"上傳錯誤",Toast.LENGTH_LONG).show();
                                            control_layout.callOnClick();
                                        }
                                    });

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context,"上傳錯誤",Toast.LENGTH_LONG).show();
                    control_layout.callOnClick();
                }

//                Log.d(TAG,convert.convert(event.getX(),event.getY()));
//                Log.d(TAG,"a="+convert.check_a()+" b="+convert.check_b()+" c="+convert.check_c()+" d="+convert.check_d());
                return false;
            }



        });
    }

    private void update_thread(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (flag_touching){
                    upload_speed_direction();
                }

                update_thread();
            }
        },500);

    }

    private void upload_speed_direction(){
        Map<String, Object> map = new HashMap<>();
        map.put("direction", direction);
        map.put("speed", speed);
        db.collection("user1").document("robot")
                .update(map);
    }

    private void set_touch_spot(float x, float y) {

        touch_spot.setX(x-53);
        touch_spot.setY(y-60);

    }


//    private void ensure_AutoSwitch_status(ToggleButton hand_switch) {
//
//        db.child("control_by").addValueEventListener(robot_data.ensure_AutoSwitch_status(hand_switch,name));
//
//    }

    private void sethand_switch(ToggleButton hand_switch) {

        String temp = "my name  : "+name+"\ncontrol by : ";

        db.collection("user1").document("robot_control_by")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        internet_now_use = value.get("control_by").toString();
//                        internet_now_use = snapshot.getValue().toString();
                        control_name.setText(String.format("%s%s", temp, internet_now_use));

                        if (internet_now_use.equals("NO_ONE")){
                            flag_no_one_use = true;
                        }
                        if (internet_now_use.equals(name)){
                            control_layout.setVisibility(View.VISIBLE);
                        }else{
                            control_layout.setVisibility(View.GONE);
                            flag_no_one_use = false;
                        }

                        hand_switch.setChecked(value.get("control_by").toString().equals(name));
//                        hand_switch.setChecked(snapshot.getValue().equals(name));

                    }
                });


        hand_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                Log.d(TAG,internet_now_use);

                if (isChecked && internet_now_use.equals("NO_ONE")) {
                    Toast.makeText(context,name + " 正在使用中",Toast.LENGTH_SHORT).show();
                    setHand(true);
                }else if(isChecked && !internet_now_use.equals("NO_ONE")){
                    Toast.makeText(context,internet_now_use + " 正在使用中",Toast.LENGTH_SHORT).show();
                    buttonView.setChecked(false);
                }else if (!isChecked && internet_now_use.equals(name)){
                    setHand(false);
                }

            }
        });
    }

//    private boolean get_internet_now_use_equals_name(){
//        return internet_now_use.equals("NO_ONE");
//    }

    public void setHand(boolean hand){
        try {
            if (!hand) {

                db.collection("user1").document("robot_control_by")
                        .update("control_by", "NO_ONE")
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "上傳切換手自動失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                                setHand(hand);
                            }
                        });

                db.collection("user1").document("robot")
                        .update("direction", 0)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "上傳切換手自動失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                                setHand(hand);
                            }
                        });

                db.collection("user1").document("robot")
                        .update("speed", 0)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "上傳切換手自動失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                                setHand(hand);
                            }
                        });

            } else {

                db.collection("user1").document("robot_control_by")
                        .update("control_by", name)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "上傳切換手自動失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                                setHand(hand);
                            }
                        });

            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "上傳切換手自動失敗，請檢察網路", Toast.LENGTH_SHORT).show();
            setHand(hand);
        }
    }


    @Override
    public void onPause() {
        hand_switch.setChecked(false);
        super.onPause();
    }

    @Override
    public void onStop() {
        hand_switch.setChecked(false);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        hand_switch.setChecked(false);
        super.onDestroy();
    }
}