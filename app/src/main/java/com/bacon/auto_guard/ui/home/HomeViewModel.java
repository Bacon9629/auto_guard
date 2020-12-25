package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bacon.auto_guard.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.content.ContentValues.TAG;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Son_Data_format>> son_list;

    Home_Data home_data = new Home_Data();

    SharedPreferences preferences;
    DataSnapshot dataSnapshot;
    String last_touch = "";

    public HomeViewModel() {

        //TODO 在這裡判斷資料有無不同 and 製作son_Adpater

        son_list = new MutableLiveData<>();
        get_internet_data();

        lastTouch_listener();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                lastTouch_listener();
//                Looper.loop();
//            }
//        }).start();

    }

    private void get_internet_data(){
        home_data.get_internet_data(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG,"get ALL => " + snapshot.toString());
//                home_data.select_parent_getData(snapshot,"房間 1");
                dataSnapshot = snapshot;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void putContext(Context context){
        preferences = context.getSharedPreferences(context.getString(R.string.preference_name)
                ,0);
        preferences.edit().putString("home_electronic","").apply();
    }

    private void lastTouch_listener() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.d(TAG,"runnable is running");
                String temp = preferences.getString("home_electronic","");
                if (!last_touch.equals(temp)){
                    last_touch = temp;
                    refresh_data();
                }
                lastTouch_listener();
            }
        },300);

    }

    private void refresh_data(){
//        Log.d(TAG,"last touch is = "+last_touch);

        //TODO 必須把setValue放在UIThread

        son_list.setValue(home_data.select_parent_getData(dataSnapshot,last_touch));

    }


    public LiveData<ArrayList<Son_Data_format>> getSonData() {
//        Log.d(TAG,"RUN2");
        return son_list;
    }
}

