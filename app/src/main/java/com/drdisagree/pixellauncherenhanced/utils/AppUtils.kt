package com.drdisagree.pixellauncherenhanced.utils

import android.content.pm.PackageManager
import com.drdisagree.pixellauncherenhanced.PLEnhanced.Companion.appContext
import com.drdisagree.pixellauncherenhanced.data.common.Constants.LAUNCHER3_PACKAGE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.PIXEL_LAUNCHER_PACKAGE

object AppUtils {
    val isPixelLauncher = isAppInstalled(PIXEL_LAUNCHER_PACKAGE)
    val isLauncher3 = isAppInstalled(LAUNCHER3_PACKAGE)

    private fun isAppInstalled(packageName: String): Boolean =
        try {
            val pm = appContext.packageManager
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            pm.getApplicationInfo(packageName, 0).enabled
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
}
