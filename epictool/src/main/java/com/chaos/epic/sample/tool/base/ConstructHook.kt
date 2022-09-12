package com.chaos.epic.sample.tool.base

import com.chaos.epic.sample.tool.EpicHookUtils
import com.chaos.epic.sample.tool.LogUtils
import com.chaos.epic.sample.tool.entity.MethodAndParamType

abstract class ConstructHook constructor() : BaseMethodHook() {

    private var methodMap: Map<String, MethodAndParamType>? = null

    constructor(methodMap: Map<String, MethodAndParamType>) : this() {
        this.methodMap = methodMap
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)

        val superClass = Class.forName(getClassName())
        val clazz: Class<Any> = param?.thisObject?.javaClass ?: return
        if (clazz != superClass) {
            LogUtils.printExtendConstructHookParam(superClass.simpleName, param)
            if (methodMap.isNullOrEmpty()) {
                return
            }
            val entries = methodMap!!.entries
            entries.forEach {
                EpicHookUtils.hookMethod(clazz, it.key, it.value.methodHook, it.value.paramType)
            }
        } else {
            LogUtils.printConstructHookParam(param)
        }

    }


    abstract fun getClassName(): String
}