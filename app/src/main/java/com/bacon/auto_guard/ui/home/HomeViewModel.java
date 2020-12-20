package com.bacon.auto_guard.ui.home;

import android.os.Handler;

import java.util.HashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<HashMap<String , String>> son_data;
    HashMap<String , String> tempData = new HashMap<>();

    public HomeViewModel() {
        son_data = new MutableLiveData<>();


        tempData.put("key1","value1");
        tempData.put("key2","value2");
        tempData.put("key3","value3");


        son_data.setValue(tempData);

        addData();

    }

    void addData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tempData.put("key ADD","value ADD");
                son_data.setValue(tempData);

                addData();
            }
        },500);
    }


    public LiveData<HashMap<String , String>> getSonData() {
        return son_data;
    }
}

