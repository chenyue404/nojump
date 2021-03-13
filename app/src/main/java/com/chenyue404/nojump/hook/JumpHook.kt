package com.chenyue404.nojump.hook

import android.app.AndroidAppHelper
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.widget.Toast
import com.chenyue404.nojump.*
import com.chenyue404.nojump.entity.LogEntity
import com.chenyue404.nojump.entity.RuleEntity
import com.crossbowffs.remotepreferences.RemotePreferences
import com.google.gson.Gson
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class JumpHook : IXposedHookLoadPackage {

    private val PACKAGE_NAME = "android"
    private val TAG = "nojump--hook-"

    companion object {
        var ruleStr = ""
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam.packageName
        val classLoader = lpparam.classLoader

        if (packageName != PACKAGE_NAME) {
            return
        }

        hookCheckBroadcastFromSystem(classLoader)

        val IApplicationThread =
            XposedHelpers.findClass("android.app.IApplicationThread", classLoader)
        val ProfilerInfo =
            XposedHelpers.findClass("android.app.ProfilerInfo", classLoader)

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
                    createCallback()
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
                    createCallback()
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
                    createCallback()
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
                    createCallback()
                )
            }
        }
    }

//23,24,25,26,27
//        com.android.server.am.ActivityManagerService
//        startActivityAsUser(IApplicationThread caller, String callingPackage,
//            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//            int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId)

//28
//        com.android.server.am.ActivityManagerService
//        startActivityAsUser(IApplicationThread caller, String callingPackage,
//            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//            int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
//            boolean validateIncomingUser)

//29
//    com.android.server.wm.ActivityTaskManagerService
//    startActivityAsUser(IApplicationThread caller, String callingPackage,
//        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
//        boolean validateIncomingUser)

//30
//    com.android.server.wm.ActivityTaskManagerService
//    startActivityAsUser(IApplicationThread caller, String callingPackage,
//        @Nullable String callingFeatureId,
//        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
//        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
//        boolean validateIncomingUser)

    /**
     * 解除系统不能发自定义广播的限制
     */
    private fun hookCheckBroadcastFromSystem(classLoader: ClassLoader) {
        val ProcessRecord =
            XposedHelpers.findClass("com.android.server.am.ProcessRecord", classLoader)
        XposedHelpers.findAndHookMethod(
            "com.android.server.am.ActivityManagerService", classLoader,
            "checkBroadcastFromSystem",
            Intent::class.java,
            ProcessRecord,
            String::class.java,
            Int::class.java,
            Boolean::class.java,
            List::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val intent: Intent = param.args[0] as Intent
                    if (intent.action == LogReceiver.ACTION) {
                        param.args[4] = true
                    }
                }
            }
        )
    }

    private fun createCallback(): XC_MethodHook {
        return object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val intent = param.args[2] as Intent
                val callingPackage = param.args[1] as String
                val dataString = intent.dataString
                val targetPackage = intent.`package`
                val field = XposedHelpers.findFieldIfExists(
                    param.thisObject.javaClass,
                    "mUiContext"
                )
                val handlerField = XposedHelpers.findFieldIfExists(
                    param.thisObject.javaClass,
                    "mUiHandler"
                )
                val myContext = AndroidAppHelper.currentApplication().createPackageContext(
                    BuildConfig.APPLICATION_ID,
                    Context.CONTEXT_IGNORE_SECURITY
                )

//                var activityRecordContext: Context? = null
//                XposedHelpers.findFieldIfExists(
//                    param.thisObject.javaClass,
//                    "mLastResumedActivity"
//                )?.let {
//                    val activityRecordPackageName = XposedHelpers.findFieldIfExists(
//                        it[param.thisObject].javaClass,
//                        "packageName"
//                    )[it[param.thisObject]]?.toString()
//                    XposedBridge.log("$TAG activityRecordPackageName=$activityRecordPackageName")
//                    activityRecordContext =
//                        AndroidAppHelper.currentApplication().createPackageContext(
//                            activityRecordPackageName,
//                            Context.CONTEXT_IGNORE_SECURITY
//                        )
//                } ?: kotlin.run {
//                    XposedBridge.log("$TAG activityRecordField为空")
//                }

                XposedBridge.log(
                    "${TAG}field=$field\n" +
                            "dataString=$dataString\n" +
                            "targetPackage=$targetPackage\n" +
                            "callPackage=$callingPackage\n" +
                            "component=${intent.component}\n" +
                            "intent=$intent"
                )
                val providerAuthority = myContext.getString(
                    getResourceIdByName(
                        myContext,
                        "provider_authority"
                    )
                )
//                activityRecordContext?.let {
//                    val beforeRuleStr = RemotePreferences(
//                        activityRecordContext,
//                        providerAuthority,
//                        MyPreferenceProvider.PREF_NAME
//                    ).getString(MyPreferenceProvider.KEY_NAME, "")
//                    XposedBridge.log(
//                        "$TAG context=${it.packageName}\n" +
//                                "ruleStr=$beforeRuleStr"
//                    )
//                }

                if (!TextUtils.isEmpty(dataString)
                    && callingPackage != targetPackage
                ) {
                    val context = field[param.thisObject] as Context
                    val mUiHandler = handlerField[param.thisObject] as Handler

                    XposedBridge.log(
                        "$TAG ruleStr读取之前=$ruleStr"
                    )
                    mUiHandler.post {
                        ruleStr = RemotePreferences(
                            context,
                            providerAuthority,
                            MyPreferenceProvider.PREF_NAME
                        ).getString(MyPreferenceProvider.KEY_NAME, "").toString()
                        XposedBridge.log(
                            "$TAG ruleStr读取=$ruleStr"
                        )
                    }

                    val ruleList = fromJson<ArrayList<RuleEntity>>(ruleStr)
                    val shouldBlock = !ruleList.isNullOrEmpty() &&
                            ruleList.any { ruleEntity ->
                                dataString!!.contains(ruleEntity.dataString) &&
                                        ruleEntity.callPackage.split(",")
                                            .any {
                                                if (ruleEntity.isBlock) {
                                                    callingPackage.contains(it)
                                                } else {
                                                    !callingPackage.contains(it)
                                                }
                                            }
                            }
                    context.sendBroadcast(Intent().apply {
                        action = LogReceiver.ACTION
                        putExtra(
                            LogReceiver.EXTRA_KEY, Gson().toJson(
                                LogEntity(
                                    System.currentTimeMillis(),
                                    callingPackage,
                                    dataString,
                                    shouldBlock
                                )
                            )
                        )
                    })
                    if (shouldBlock) {
                        val blockTip = myContext.getString(
                            getResourceIdByName(
                                myContext,
                                "blocked"
                            )
                        )
                        mUiHandler.post {
                            Toast.makeText(context, blockTip, Toast.LENGTH_SHORT).show()
                        }
                        param.result = 0
                    }
                }
            }
        }
    }
}