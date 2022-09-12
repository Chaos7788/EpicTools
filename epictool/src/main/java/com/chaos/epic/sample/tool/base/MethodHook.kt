package com.chaos.epic.sample.tool.base

import com.chaos.epic.sample.tool.LogUtils

class MethodHook : BaseMethodHook() {

    override fun beforeHookedMethod(param: MethodHookParam?) {
        LogUtils.printBeforeMethodHookParam(param)
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        LogUtils.printAfterMethodHookParam(param)
    }

}