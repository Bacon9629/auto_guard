package com.bacon.auto_guard.ui.robot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RobotViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RobotViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is robot fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}