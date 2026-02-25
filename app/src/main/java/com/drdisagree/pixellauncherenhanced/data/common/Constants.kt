package com.drdisagree.pixellauncherenhanced.data.common

import com.drdisagree.pixellauncherenhanced.BuildConfig
import com.drdisagree.pixellauncherenhanced.xposed.utils.BootLoopProtector

object Constants {
    // Shared Preference File
    const val SHARED_PREFERENCES = "${BuildConfig.APPLICATION_ID}_preferences"

    // System packages
    const val FRAMEWORK_PACKAGE = "android"
    const val PIXEL_LAUNCHER_PACKAGE = "com.google.android.apps.nexuslauncher"
    const val LAUNCHER3_PACKAGE = "com.android.launcher3"

    // Preferences
    const val VIBRATE_UI = "vibrate_ui"
    const val XPOSED_HOOK_CHECK = "xposed_hook_check"
    const val ACTION_HOOK_CHECK_REQUEST = "${BuildConfig.APPLICATION_ID}.ACTION_HOOK_CHECK_REQUEST"
    const val ACTION_HOOK_CHECK_RESULT = "${BuildConfig.APPLICATION_ID}.ACTION_HOOK_CHECK_RESULT"
const val DOUBLE_TAP_TO_SLEEP = "xposed_doubletaptosleep"
    const val HIDE_AT_A_GLANCE = "xposed_hideataglance"
    const val DESKTOP_SEARCH_BAR = "xposed_desktopsearchbar"
    const val RESTART_LAUNCHER = "xposed_restartlauncher"
    const val DEVELOPER_OPTIONS = "xposed_developeroptions"
    const val ENTRY_IN_LAUNCHER_SETTINGS = "xposed_entryinlaunchersettings"
    const val ENTRY_IN_OPTIONS_POPUP = "xposed_entryinoptionspopup"
    const val LOCK_LAYOUT = "xposed_locklayout"
    const val REMOVE_ICON_BADGE = "xposed_removeiconbadge"
    const val HIDE_GESTURE_PILL = "xposed_hidegesturepill"
    const val NAVIGATION_SPACE_HEIGHT = "xposed_navigationspaceheight"

    val PREF_UPDATE_EXCLUSIONS =
        listOf(
            BootLoopProtector.LOAD_TIME_KEY_KEY,
            BootLoopProtector.PACKAGE_STRIKE_KEY_KEY,
        )
}
