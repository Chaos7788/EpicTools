package com.chaos.epic.api;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EpicHookManager {

    public static void init() {

        try {
            Class<?> epicHookCollection = Class.forName("com.chaos.epic.tools.EpicHookCollection");
            Method method = epicHookCollection.getMethod("init");
            method.invoke(epicHookCollection);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            e.printStackTrace();
        }
        //findViewById
//        EpicHookCollection.init();
//        EpicHookUtils.hookMethod(View.class, "findViewById", new MethodHook(), int.class);
    }

}
