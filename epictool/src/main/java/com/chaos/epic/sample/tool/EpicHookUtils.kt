package com.chaos.epic.sample.tool

import android.util.Log
import com.chaos.epic.sample.tool.base.BaseMethodHook
import com.chaos.epic.sample.tool.base.ConstructHook
import de.robv.android.xposed.DexposedBridge

object EpicHookUtils {

    @JvmStatic
    fun <T> hookAllConstruct(className: String, methodHook: ConstructHook) {
        val aClass: Class<T>? = findClass(className)
        aClass?.also {
            hookAllConstruct(it, methodHook)
        }
    }
    @JvmStatic
    fun <T> hookAllConstruct(clazz: Class<T>, methodHook: ConstructHook) {
        DexposedBridge.hookAllConstructors(clazz, methodHook)
    }

    @JvmStatic
    fun <T> hookMethod(
        className: String,
        methodName: String,
        methodHook: BaseMethodHook,
        vararg paramTypes: Any
    ) {
        val aClass = findClass<T>(className)
        aClass?.also {
            hookMethod<T>(aClass, methodName, methodHook, *paramTypes)
        }
    }

    @JvmStatic
    fun <T> hookMethod(
        clazz: Class<T>,
        methodName: String,
        methodHook: BaseMethodHook,
        vararg paramTypes: Any
    ) {
        val any: Array<Any> = if (paramTypes.isNullOrEmpty()) {
            Array(1) {
                methodHook
            }

        } else {
            val size = paramTypes.size
            Array(size + 1) {
                if (it < size) {
                    paramTypes[it]
                } else {
                    methodHook
                }
            }
        }
        DexposedBridge.findAndHookMethod(clazz, methodName, *any)
    }


    private fun <T> findClass(className: String): Class<T>? {
        try {
            return Class.forName(className) as Class<T>
        } catch (e: ClassNotFoundException) {
            Log.e(HookConstants.HOOK_TAG_CONSTRUCT, "findClass error", e)
        }
        return null
    }
}