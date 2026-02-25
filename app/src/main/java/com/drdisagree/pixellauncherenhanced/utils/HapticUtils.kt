package com.drdisagree.pixellauncherenhanced.utils

import android.view.HapticFeedbackConstants
import android.view.View
import com.drdisagree.pixellauncherenhanced.data.common.Constants.VIBRATE_UI
import com.drdisagree.pixellauncherenhanced.data.config.RPrefs

object HapticUtils {
    enum class VibrationType {
        Weak,
        Strong,
    }

    private fun View.vibrate(type: VibrationType) {
        if (RPrefs.getBoolean(VIBRATE_UI, true)) {
            when (type) {
                VibrationType.Weak -> performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                VibrationType.Strong -> performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }
    }

    fun View.weakVibrate() {
        vibrate(VibrationType.Weak)
    }

    fun View.strongVibrate() {
        vibrate(VibrationType.Strong)
    }
}
