package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bacon.auto_guard.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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
//    DataSnapshot dataSnapshot;
    QuerySnapshot QuerySnapshot;
    String last_touch = "";

    Handler handler;
    Runnable runUI;

    Context context;



    public HomeViewModel() {

        //TODO 在這裡判斷資料有無不同 and 製作son_Adpater

        son_list = new MutableLiveData<>();
        get_internet_data();
        handler = new Handler();


//        lastTouch_listener();

        //如果資料庫有變動 => refreshData
        runUI = new Runnable() {
            @Override
            public void run() {
                refresh_data();
//                Log.d(TAG,"runnable is running");
            }
        };

//        handler.post(runUI);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                lastTouch_listener();
                Looper.loop();
            }
        });

        thread.start();

    }

    private void get_internet_data(){

        home_data.get_internet_data(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                QuerySnapshot = value;
                Log.d(TAG, value.getDocumentChanges().toString());
                refresh_data();
            }
        });

    }

    public void putContext(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(context.getString(R.string.preference_name)
                ,0);
        preferences.edit().putString("home_electronic","").apply();
    }

    private void lastTouch_listener() {

//                Log.d(TAG,"runnable is running");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String temp = preferences.getString("home_electronic","");
                if (!last_touch.equals(temp)) {
                    last_touch = temp;
                    handler.post(runUI);
                }


                lastTouch_listener();

            }
        },100);


    }

    private void refresh_data(){

        //把資料重新設定給recycler view

//        Log.d(TAG,"last touch is = "+last_touch);

        //必須把setValue放在UIThread

        son_list.setValue(home_data.select_parent_getData(context, QuerySnapshot, last_touch));

    }


    public LiveData<ArrayList<Son_Data_format>> getSonData() {
//        Log.d(TAG,"RUN2");
        return son_list;
    }
}

