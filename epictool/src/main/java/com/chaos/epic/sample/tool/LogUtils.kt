package com.chaos.epic.sample.tool

import android.os.Looper
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import java.lang.StringBuilder

object LogUtils {

    /**
     * 主线程堆栈
     */
    fun printMainThreadStack(tag: String) {
        val thread = Looper.getMainLooper().thread
        printThreadStack(tag, thread)
    }
    /**
     * 当前线程堆栈
     */
    fun printCurrentThreadStack(tag: String) {
        printThreadStack(tag, Thread.currentThread())
    }

    /**
     * hook调用后
     */
    fun printAfterMethodHookParam(param: XC_MethodHook.MethodHookParam?) {
        printHookParam(HookConstants.HOOK_TAG_METHOD, "afterHookMethod", param)
    }

    /**
     * hook调用前
     */
    fun printBeforeMethodHookParam(param: XC_MethodHook.MethodHookParam?) {
        printHookParam(HookConstants.HOOK_TAG_METHOD, "beforeHookMethod", param)
    }

    /**
     * 构造方法
     */
    fun printConstructHookParam(param: XC_MethodHook.MethodHookParam?) {
        printHookParam(HookConstants.HOOK_TAG_CONSTRUCT, "create instance", param)
    }

    /**
     * 继承后调用父类构造方法
     */
    fun printExtendConstructHookParam(className: String, param: XC_MethodHook.MethodHookParam?) {
        printHookParam(HookConstants.HOOK_TAG_CONSTRUCT, "found class extend", param)
    }

    fun printHookParam(tag: String, name: String, param: XC_MethodHook.MethodHookParam?) {
        param?.apply {
            Log.i(
                tag, String.format(
                    "%s, class = %s, method = %s",
                    name,
                    thisObject.javaClass.name,
                    method.name
                )
            )
        }
    }

    fun printThreadStack(tag: String, thread: Thread) {
        val stackTrace = thread.stackTrace
        val sb = StringBuilder("Call Stack:\n")
        stackTrace.forEach {
            sb.append("\tat ").append(it).append("\n")

            Log.i(tag, sb.toString())
        }
    }
}