package com.bacon.auto_guard.ui.robot;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
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

import static android.content.ContentValues.TAG;

public class RobotFragment extends Fragment {

    private RobotViewModel robotViewModel;
    private DatabaseReference db;
    private Context context;
    private Robot_Data robot_data;
    private String name;

    ConstraintLayout control_layout;
    ToggleButton auto_switch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        robotViewModel =
                new ViewModelProvider(this).get(RobotViewModel.class);
        View root = inflater.inflate(R.layout.fragment_robot, container, false);

        context = getActivity();
        db = FirebaseDatabase.getInstance().getReference("robot");

        robot_data = new Robot_Data(context);
        TextView control_name = root.findViewById(R.id.control_name);
        name = context.getSharedPreferences("activity",0).getString("name","無名氏");
        control_name.setText(name);

        control_layout = root.findViewById(R.id.control_arrow_layout);

        boolean[] direction = {false,false};
        Log.d(TAG,control_layout.getMinHeight()+"  "+control_layout.getMaxHeight()+"");


        // X=245，Y=230

        control_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        Log.d(TAG,"down");
                        break;
                    }

                    case MotionEvent.ACTION_MOVE:{
                        Log.d(TAG,"X = "+event.getX()+" Y = "+event.getY());
                        break;
                    }

                    case MotionEvent.ACTION_UP:{
                        Log.d(TAG,"up");
                    }



                }



                return false;
            }

        });






        auto_switch = root.findViewById(R.id.control_auto_switch);

        setAuto_switch(auto_switch);
//        ensure_AutoSwitch_status(auto_switch);




        robotViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return root;
    }

//    private void ensure_AutoSwitch_status(ToggleButton auto_switch) {
//
//        db.child("control_by").addValueEventListener(robot_data.ensure_AutoSwitch_status(auto_switch,name));
//
//    }

    private void setAuto_switch(ToggleButton auto_switch) {


        db.child("control_by").addValueEventListener(robot_data.check_internet_Auto(auto_switch,name));


        auto_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAuto(isChecked);

                if (isChecked){
                    control_layout.setVisibility(View.GONE);
                }else{
                    control_layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setAuto(boolean auto){
        try {
            if (auto) {
                db.child("control_by").setValue("NO_ONE")
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "上傳切換失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                                setAuto(auto);
                            }
                        });
            } else {
                db.child("control_by").setValue(name)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "上傳切換失敗，請檢察網路", Toast.LENGTH_SHORT).show();
                                setAuto(auto);
                            }
                        });
            }
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "上傳切換失敗，請檢察網路", Toast.LENGTH_SHORT).show();
            setAuto(auto);
        }
    }

    private void set_auto_ON(){
        auto_switch.setChecked(true);
    }

    @Override
    public void onPause() {
        set_auto_ON();
        super.onPause();
    }

    @Override
    public void onStop() {
        set_auto_ON();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        set_auto_ON();
        super.onDestroy();
    }
}