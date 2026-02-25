package com.drdisagree.pixellauncherenhanced.ui.base

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.drdisagree.pixellauncherenhanced.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.shape.MaterialShapeDrawable

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setupEdgeToEdge()
        super.onCreate(savedInstanceState)
    }

    private fun setupEdgeToEdge() {
        try {
            (findViewById<View>(R.id.appBarLayout) as AppBarLayout).statusBarForeground =
                MaterialShapeDrawable.createWithElevationOverlay(
                    applicationContext,
                )
        } catch (_: Exception) {
        }

        val window = window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (getResources().configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val viewGroup = getWindow().decorView.findViewById<ViewGroup>(android.R.id.content)

            ViewCompat.setOnApplyWindowInsetsListener(viewGroup) { v: View, windowInsets: WindowInsetsCompat ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                val params = v.layoutParams as MarginLayoutParams

                v.setPadding(
                    params.leftMargin + insets.left,
                    0,
                    params.rightMargin + insets.right,
                    0,
                )

                params.topMargin = 0
                params.bottomMargin = 0

                v.layoutParams = params

                windowInsets
            }
        }
    }

    @Suppress("deprecation")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
