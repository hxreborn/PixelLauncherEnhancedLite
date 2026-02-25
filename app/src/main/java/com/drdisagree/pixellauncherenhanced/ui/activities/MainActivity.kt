package com.drdisagree.pixellauncherenhanced.ui.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.drdisagree.pixellauncherenhanced.R
import com.drdisagree.pixellauncherenhanced.databinding.ActivityMainBinding
import com.drdisagree.pixellauncherenhanced.ui.base.BaseActivity
import com.drdisagree.pixellauncherenhanced.ui.fragments.HomePage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class MainActivity :
    BaseActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        if (savedInstanceState == null) {
            replaceFragment(supportFragmentManager, HomePage())
        }

        val isRooted = intent.getBooleanExtra("isRooted", false)

        if (!isRooted) {
            MaterialAlertDialogBuilder(
                this@MainActivity,
                R.style.MaterialComponents_MaterialAlertDialog,
            ).setCancelable(false)
                .setTitle(getText(R.string.root_connection_failed_title))
                .setMessage(getText(R.string.root_connection_failed_desc))
                .setPositiveButton(getText(R.string.exit)) { dialog, i -> exitProcess(0) }
                .show()
        }
    }

    @Suppress("deprecation")
    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference,
    ): Boolean {
        replaceFragment(
            supportFragmentManager,
            supportFragmentManager.fragmentFactory
                .instantiate(
                    classLoader,
                    pref.fragment!!,
                ).apply {
                    arguments = pref.extras
                    setTargetFragment(caller, 0)
                },
        )
        return true
    }

    companion object {
        fun replaceFragment(
            fragmentManager: FragmentManager,
            fragment: Fragment,
        ) {
            if (fragmentManager.isStateSaved) return

            try {
                val fragmentTag = fragment.javaClass.simpleName
                val currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView)

                if (currentFragment != null && currentFragment.javaClass.simpleName == fragmentTag) {
                    popCurrentFragment(fragmentManager)
                }

                for (i in 0 until fragmentManager.backStackEntryCount) {
                    if (fragmentManager.getBackStackEntryAt(i).name == fragmentTag) {
                        fragmentManager.popBackStack(
                            fragmentTag,
                            POP_BACK_STACK_INCLUSIVE,
                        )
                        break
                    }
                }

                fragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                    )

                    replace(R.id.fragmentContainerView, fragment, fragmentTag)

                    when (fragmentTag) {
                        HomePage::class.java.simpleName -> {
                            fragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
                        }

                        else -> {
                            addToBackStack(fragmentTag)
                        }
                    }

                    commit()
                }
            } catch (_: IllegalStateException) {
            }
        }

        fun popCurrentFragment(fragmentManager: FragmentManager) {
            if (fragmentManager.isStateSaved) return

            fragmentManager.popBackStack()
        }
    }
}
