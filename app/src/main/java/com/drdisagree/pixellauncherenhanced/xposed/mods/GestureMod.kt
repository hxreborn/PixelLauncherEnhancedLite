package com.drdisagree.pixellauncherenhanced.xposed.mods

import android.content.Context
import com.drdisagree.pixellauncherenhanced.data.common.Constants.DOUBLE_TAP_TO_SLEEP
import com.drdisagree.pixellauncherenhanced.xposed.HookEntry.Companion.enqueueProxyCommand
import com.drdisagree.pixellauncherenhanced.xposed.ModPack
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.VibrationUtils
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.XposedHook.Companion.findClass
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.hookMethod
import com.drdisagree.pixellauncherenhanced.xposed.utils.XPrefs.Xprefs
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class GestureMod(
    context: Context,
) : ModPack(context) {
    private var doubleTapToSleep = false

    override fun updatePrefs(vararg key: String) {
        doubleTapToSleep = Xprefs.getBoolean(DOUBLE_TAP_TO_SLEEP, false)
    }

    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        val workspaceTouchListenerClass =
            findClass("com.android.launcher3.touch.WorkspaceTouchListener")

        // onDoubleTap is inherited from SimpleOnGestureListener, not declared
        // in WorkspaceTouchListener. hookAllMethods only searches declaredMethods,
        // so we hook on the declaring class and filter by instance.
        val declaringClass = workspaceTouchListenerClass?.methods
            ?.find { it.name == "onDoubleTap" }
            ?.declaringClass ?: return

        declaringClass.hookMethod("onDoubleTap").runAfter { param ->
            if (!doubleTapToSleep) return@runAfter
            if (workspaceTouchListenerClass?.isInstance(param.thisObject) != true) return@runAfter

            VibrationUtils.triggerVibration(mContext, 2)
            enqueueProxyCommand { proxy ->
                proxy.runCommand("input keyevent 223")
            }
        }
    }
}
