package com.bacon.auto_guard.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.content.ContentValues.TAG;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<HashMap<String,String>>> son_list;

    Home_Data home_data_temp = new Home_Data();

    public HomeViewModel() {

        //TODO 在這裡判斷資料有無不同 and 製作son_Adpater

        son_list = new MutableLiveData<>();

//        home_data_temp

        refresh_data();




    }

    private void refresh_data() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String,String>> temp = home_data_temp.getSon_data();
//                Log.d(TAG,temp.size()+" modlee");
                if (temp != son_list.getValue()){

                    son_list.setValue(temp);
                }
                refresh_data();
            }
        },300);

    }


    public LiveData<ArrayList<HashMap<String,String>>> getSonData() {
        Log.d(TAG,"RUN2");
        return son_list;
    }
}

