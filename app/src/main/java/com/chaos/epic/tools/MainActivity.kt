package com.chaos.epic.tools

import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaos.epic.sample.tool.EpicHookUtils
import com.chaos.epic.sample.tool.base.ConstructHook

class MainActivity : AppCompatActivity() {

    private val hook by lazy {
        object : ConstructHook() {
            override fun getClassName(): String {
                return HookToolConstants.HookView.TEXT_VIEW
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EpicHookUtils.hookAllConstruct(TextView::class.java, hook)
        setContentView(R.layout.activity_main)
    }
}