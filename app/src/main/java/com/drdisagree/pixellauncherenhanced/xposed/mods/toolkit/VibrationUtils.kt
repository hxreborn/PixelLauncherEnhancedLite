package com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object VibrationUtils {
    private val executor: Executor = Executors.newSingleThreadExecutor()

    @SuppressLint("MissingPermission")
    @Suppress("deprecation")
    fun triggerVibration(
        context: Context,
        intensity: Int,
    ) {
        executor.execute(
            Runnable {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (intensity == 0) {
                    return@Runnable
                }

                val effect = createVibrationEffect(intensity) ?: return@Runnable
                vibrator.cancel()
                vibrator.vibrate(effect)
            },
        )
    }

    private fun createVibrationEffect(intensity: Int): VibrationEffect? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (intensity) {
                2 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                3 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                4 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                5 -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                else -> null
            }
        } else {
            val amplitude =
                when (intensity) {
                    2 -> 30
                    3 -> 60
                    4 -> 90
                    5 -> 120
                    else -> return null
                }
            VibrationEffect.createOneShot(100, amplitude)
        }
    }
}
