package com.bacon.auto_guard.ui.home;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mText.setValue("second");
            }
        }, 2000); //2000ms
    }

    public void secondTouch(){

    }

    public LiveData<String> getText() {
        return mText;
    }
}