package com.chaos.epic.tools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chaos.epic.api.EpicHookManager

class MainActivity : AppCompatActivity() {

//    private val hook by lazy {
//        object : ConstructHook() {
//            override fun getClassName(): String {
//                return HookToolConstants.HookView.TEXT_VIEW
//            }
//
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        EpicHookUtils.hookAllConstruct(TextView::class.java, hook)
//        EpicHookUtils.hookMethod(View::class.java, "findViewById", MethodHook(), Int::class.java)
        EpicHookManager.init()
        setContentView(R.layout.activity_main)


    }
}