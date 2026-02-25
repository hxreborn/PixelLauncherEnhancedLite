package com.drdisagree.pixellauncherenhanced.xposed.utils

import android.content.Context
import com.crossbowffs.remotepreferences.RemotePreferences
import com.drdisagree.pixellauncherenhanced.ui.preferences.SliderPreference

@Suppress("unused")
class ExtendedRemotePreferences : RemotePreferences {
    constructor(context: Context, authority: String, prefFileName: String) : super(
        context,
        authority,
        prefFileName,
    )

    constructor(
        context: Context,
        authority: String,
        prefFileName: String,
        strictMode: Boolean,
    ) : super(context, authority, prefFileName, strictMode)

    fun getBoolean(key: String?): Boolean = getBoolean(key, false)

    fun getSliderInt(
        key: String?,
        defaultVal: Int,
    ): Int = SliderPreference.getSingleIntValue(this, key, defaultVal)

    fun getSliderFloat(
        key: String?,
        defaultVal: Float,
    ): Float = SliderPreference.getSingleFloatValue(this, key, defaultVal)

    fun getSliderValues(
        key: String?,
        defaultValue: Float,
    ): List<Float> = SliderPreference.getValues(this, key, defaultValue)

    fun getListString(
        key: String?,
        defaultValue: String,
    ): String? = getString(key, defaultValue)
}
