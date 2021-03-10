package com.chenyue404.nojump.hook

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.widget.Toast
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class JumpHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "android"
    private val TAG = "nojump-hook-"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        val IApplicationThread =
            XposedHelpers.findClass("android.app.IApplicationThread", classLoader)
        val ProfilerInfo =
            XposedHelpers.findClass("android.app.ProfilerInfo", classLoader)

        val parameterTypesAndCallback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val intent = param.args[2] as Intent
                val targetActivity =
                    if (intent.component == null) "" else intent.component!!
                        .className
                val targetApp = intent.getPackage()
                val callingPackage = param.args[1] as String
                val dataString = intent.dataString
                val scheme = intent.scheme
                val field = XposedHelpers.findFieldIfExists(
                    param.thisObject.javaClass,
                    "mUiContext"
                )
                val handlerField = XposedHelpers.findFieldIfExists(
                    param.thisObject.javaClass,
                    "mUiHandler"
                )
                if (field != null && !TextUtils.isEmpty(scheme)
                    && scheme == "openapp.jdmobile"
                ) {
                    try {
                        val context = field[param.thisObject] as Context
                        val mUiHandler = handlerField[param.thisObject] as Handler
                        mUiHandler.post {
                            XposedBridge.log(TAG + "执行Toast")
                            Toast.makeText(
                                context,
                                "$TAG 执行Toast",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        param.result = 0
                    } catch (e: IllegalAccessException) {
                        XposedBridge.log(TAG + e.toString())
                        e.printStackTrace()
                    }
                } else {
                    XposedBridge.log(TAG + "field为空")
                }
                XposedBridge.log(
                    TAG + "--callingPackage=" + callingPackage
                            + "--targetActivity=" + targetActivity
                            + "--targetApp=" + targetApp
                            + "--dataString=" + dataString
                            + "--scheme=" + scheme
                )
            }
        }

        when (Build.VERSION.SDK_INT) {
            in Build.VERSION_CODES.M..Build.VERSION_CODES.O_MR1 -> {
                XposedHelpers.findAndHookMethod(
                    "com.android.server.am.ActivityManagerService",
                    classLoader,
                    "startActivityAsUser",
                    IApplicationThread,
                    String::class.java,
                    Intent::class.java,
                    String::class.java,
                    IBinder::class.java,
                    String::class.java,
                    Int::class.java,
                    Int::class.java,
                    ProfilerInfo,
                    Bundle::class.java,
                    Int::class.java,
                    parameterTypesAndCallback
                )
            }
            Build.VERSION_CODES.P -> {
                XposedHelpers.findAndHookMethod(
                    "com.android.server.am.ActivityManagerService",
                    classLoader,
                    "startActivityAsUser",
                    IApplicationThread,
                    String::class.java,
                    Intent::class.java,
                    String::class.java,
                    IBinder::class.java,
                    String::class.java,
                    Int::class.java,
                    Int::class.java,
                    ProfilerInfo,
                    Bundle::class.java,
                    Int::class.java,
                    Boolean::class.java,
                    parameterTypesAndCallback
                )
            }
            Build.VERSION_CODES.Q -> {
                XposedHelpers.findAndHookMethod(
                    "com.android.server.wm.ActivityTaskManagerService",
                    classLoader,
                    "startActivityAsUser",
                    IApplicationThread,
                    String::class.java,
                    Intent::class.java,
                    String::class.java,
                    IBinder::class.java,
                    String::class.java,
                    Int::class.java,
                    Int::class.java,
                    ProfilerInfo,
                    Bundle::class.java,
                    Int::class.java,
                    Boolean::class.java,
                    parameterTypesAndCallback
                )
            }
            Build.VERSION_CODES.R -> {
                XposedHelpers.findAndHookMethod(
                    "com.android.server.wm.ActivityTaskManagerService",
                    classLoader,
                    "startActivityAsUser",
                    IApplicationThread,
                    String::class.java,
                    String::class.java,
                    Intent::class.java,
                    String::class.java,
                    IBinder::class.java,
                    String::class.java,
                    Int::class.java,
                    Int::class.java,
                    ProfilerInfo,
                    Bundle::class.java,
                    Int::class.java,
                    Boolean::class.java,
                    parameterTypesAndCallback
                )
            }
        }
    }

//23,24,25,26,27
//    android.app.IApplicationThread
//    android.app.ProfilerInfo
//        com.android.server.am.ActivityManagerService
//        startActivityAsUser(IApplicationThread caller, String callingPackage,
//            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//            int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId)

//28
//    android.app.IApplicationThread
//    android.app.ProfilerInfo
//        com.android.server.am.ActivityManagerService
//        startActivityAsUser(IApplicationThread caller, String callingPackage,
//            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//            int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
//            boolean validateIncomingUser)

//29
//    android.app.IApplicationThread
//    android.app.ProfilerInfo
//    com.android.server.wm.ActivityTaskManagerService
//    startActivityAsUser(IApplicationThread caller, String callingPackage,
//        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
//        boolean validateIncomingUser)

//30
//    android.app.IApplicationThread
//    android.app.ProfilerInfo
//    com.android.server.wm.ActivityTaskManagerService
//    startActivityAsUser(IApplicationThread caller, String callingPackage,
//        @Nullable String callingFeatureId,
//        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
//        boolean validateIncomingUser)
}