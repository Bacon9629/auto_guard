package com.bacon.auto_guard.ui.robot;

import android.content.Context;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

class Robot_Data {
    DatabaseReference db = FirebaseDatabase.getInstance().getReference("robot");

    Context context;

    public Robot_Data(Context context){
        this.context = context;
    }

    public ValueEventListener ensure_AutoSwitch_status(ToggleButton auto_switch,String name){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                auto_switch.setChecked(!snapshot.getValue().equals(name));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public ValueEventListener check_internet_Auto(ToggleButton button,String name){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                button.setChecked(!snapshot.getValue().equals(name));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
//    public void setAuto(boolean auto){
//        try {
//            if (auto) {
//                db.child("mode").setValue("auto")
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                e.printStackTrace();
//                                Toast.makeText(context, "上傳切換失敗，請檢察網路", Toast.LENGTH_SHORT).show();
//                                setAuto(auto);
//                            }
//                        });
//            } else {
//                db.child("mode").setValue("manual")
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                e.printStackTrace();
//                                Toast.makeText(context, "上傳切換失敗，請檢察網路", Toast.LENGTH_SHORT).show();
//                                setAuto(auto);
//                            }
//                        });
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(context, "上傳切換失敗，請檢察網路", Toast.LENGTH_SHORT).show();
//            setAuto(auto);
//        }
//    }


}
