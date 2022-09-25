package com.chaos.epic.api.hook;

import android.util.Log;

import com.chaos.epic.annotation.AfterMethod;
import com.chaos.epic.annotation.BeforeMethod;
import com.chaos.epic.annotation.EpicMethodParam;

@EpicMethodParam(className = "android.view.View", methodName = "findViewById", paramType = {int.class})
public class ViewHook {


    private static final String TAG = "ViewHook";

    @BeforeMethod
    public void doBefore() {
        Log.d(TAG, "doBefore: ");
    }


    @AfterMethod
    public void doAfterMethod() {
        Log.d(TAG, "doAfterMethod: ");
    }
}

