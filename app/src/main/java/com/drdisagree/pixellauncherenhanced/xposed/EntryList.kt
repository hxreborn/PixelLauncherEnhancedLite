package com.drdisagree.pixellauncherenhanced.xposed

import com.drdisagree.pixellauncherenhanced.data.common.Constants.LAUNCHER3_PACKAGE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.PIXEL_LAUNCHER_PACKAGE
import com.drdisagree.pixellauncherenhanced.xposed.mods.GestureMod
import com.drdisagree.pixellauncherenhanced.xposed.mods.HotseatMod
import com.drdisagree.pixellauncherenhanced.xposed.mods.LauncherSettings
import com.drdisagree.pixellauncherenhanced.xposed.mods.LauncherUtils
import com.drdisagree.pixellauncherenhanced.xposed.mods.LockLayout
import com.drdisagree.pixellauncherenhanced.xposed.mods.IconLabels
import com.drdisagree.pixellauncherenhanced.xposed.mods.ShortcutBadge
import com.drdisagree.pixellauncherenhanced.xposed.mods.SmartSpace
import com.drdisagree.pixellauncherenhanced.xposed.mods.TaskbarHandle
import com.drdisagree.pixellauncherenhanced.xposed.utils.BroadcastHook

object EntryList {
    private val launcherModPacks: List<Class<out ModPack>> =
        listOf(
            BroadcastHook::class.java,
            LauncherUtils::class.java,
            GestureMod::class.java,
            HotseatMod::class.java,
            SmartSpace::class.java,
            LauncherSettings::class.java,
            LockLayout::class.java,
            TaskbarHandle::class.java,
            ShortcutBadge::class.java,
            IconLabels::class.java,
        )

    fun getEntries(packageName: String): ArrayList<Class<out ModPack>> {
        val modPacks = ArrayList<Class<out ModPack>>()

        when (packageName) {
            PIXEL_LAUNCHER_PACKAGE,
            LAUNCHER3_PACKAGE,
            -> {
                modPacks.addAll(launcherModPacks)
            }
        }

        return modPacks
    }
}
