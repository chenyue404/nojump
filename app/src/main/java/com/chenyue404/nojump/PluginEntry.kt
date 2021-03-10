package com.chenyue404.nojump

import com.chenyue404.nojump.hook.HookManager
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class PluginEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        HookManager.startHook(lpparam)
    }
}