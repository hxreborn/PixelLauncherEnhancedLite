package com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit

import android.content.Context
import android.util.TypedValue

object Helpers {
    fun Context.toPx(dp: Int): Int =
        TypedValue
            .applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics,
            ).toInt()
}
