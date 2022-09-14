package com.chaos.epic.api.hook;

import com.chaos.epic.annotation.AfterMethod;
import com.chaos.epic.annotation.BeforeMethod;
import com.chaos.epic.annotation.EpicParam;

@EpicParam
public class ViewHook {

    @BeforeMethod
    public void doBefore() {

    }

    @AfterMethod
    public void doAfterMethod() {

    }
}

