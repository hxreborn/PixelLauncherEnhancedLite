package com.drdisagree.pixellauncherenhanced.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar

object MiscUtils {
    fun setToolbarTitle(
        context: Context,
        @StringRes title: Int,
        showBackButton: Boolean,
        toolbar: MaterialToolbar?,
        collapsingToolbarLayout: CollapsingToolbarLayout?,
    ) {
        (context as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = context.supportActionBar
        if (actionBar != null) {
            context.supportActionBar!!.setTitle(title)
            context.supportActionBar!!.setDisplayHomeAsUpEnabled(showBackButton)
            context.supportActionBar!!.setDisplayShowHomeEnabled(showBackButton)
        }
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.title = context.getString(title)
        }
    }
}
