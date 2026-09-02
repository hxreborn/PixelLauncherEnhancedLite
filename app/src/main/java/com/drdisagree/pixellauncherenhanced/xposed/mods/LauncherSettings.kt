package com.drdisagree.pixellauncherenhanced.xposed.mods

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.drdisagree.pixellauncherenhanced.BuildConfig
import com.drdisagree.pixellauncherenhanced.R
import com.drdisagree.pixellauncherenhanced.data.common.Constants.DEVELOPER_OPTIONS
import com.drdisagree.pixellauncherenhanced.data.common.Constants.ENTRY_IN_LAUNCHER_SETTINGS
import com.drdisagree.pixellauncherenhanced.data.common.Constants.ENTRY_IN_OPTIONS_POPUP
import com.drdisagree.pixellauncherenhanced.data.common.Constants.LAUNCHER3_PACKAGE
import com.drdisagree.pixellauncherenhanced.data.common.Constants.PIXEL_LAUNCHER_PACKAGE
import com.drdisagree.pixellauncherenhanced.xposed.HookRes.Companion.modRes
import com.drdisagree.pixellauncherenhanced.xposed.ModPack
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.XposedHook.Companion.findClass
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.callMethod
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.callMethodSilently
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.getField
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.getFieldSilently
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.hasMethod
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.hookConstructor
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.hookMethod
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.log
import com.drdisagree.pixellauncherenhanced.xposed.mods.toolkit.setField
import com.drdisagree.pixellauncherenhanced.xposed.utils.XPrefs.Xprefs
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.Proxy
import java.util.Arrays

class LauncherSettings(
    context: Context,
) : ModPack(context) {
    private var devOptionsEnabled = false
    private var entryInLauncher = true
    private var entryInPopup = false

    override fun updatePrefs(vararg key: String) {
        Xprefs.apply {
            devOptionsEnabled = getBoolean(DEVELOPER_OPTIONS, false)
            entryInLauncher = getBoolean(ENTRY_IN_LAUNCHER_SETTINGS, true)
            entryInPopup = getBoolean(ENTRY_IN_OPTIONS_POPUP, false)
        }
    }

    @Suppress("deprecation")
    @SuppressLint("DiscouragedApi", "UseCompatLoadingForDrawables")
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        val launcherSettingsFragmentClass =
            findClass(
                $$"com.android.launcher3.SettingsActivity$LauncherSettingsFragment",
                $$"com.android.launcher3.settings.SettingsActivity$LauncherSettingsFragment",
            )
        val featureFlagsClass = findClass("com.android.launcher3.config.FeatureFlags")

        if (mContext.packageName == PIXEL_LAUNCHER_PACKAGE) {
            launcherSettingsFragmentClass.hookMethod("initPreference").runBefore { param ->
                val preference = param.args[0]
                val key =
                    preference.callMethodSilently("getKey")
                        ?: preference.getField("mKey") as String

                if (key == "pref_developer_options") {
                    param.result = devOptionsEnabled
                }
            }

            featureFlagsClass.hookMethod("showFlagTogglerUi").suppressError().runBefore { param ->
                param.result = devOptionsEnabled
            }
        }

        val preferenceClass = findClass("androidx.preference.Preference")!!
        var preferenceClickListenerFieldName: String? = null
        val preferenceClickListenerClass: Class<*>? =
            preferenceClass.methods
                .firstOrNull { it.name == "setOnPreferenceClickListener" }
                ?.parameterTypes
                ?.firstOrNull()
                ?: preferenceClass.declaredFields
                    .firstOrNull { field ->
                        field.name.endsWith(
                            "OnClickListener",
                            ignoreCase = true,
                        ) || field.name.endsWith("OnPreferenceClickListener", ignoreCase = true)
                    }?.also { field ->
                        preferenceClickListenerFieldName = field.name
                    }?.type

        val preferenceStyledConstructor =
            preferenceClass.declaredConstructors.firstOrNull {
                it.parameterTypes.contentEquals(
                    arrayOf(Context::class.java, AttributeSet::class.java),
                )
            }

        launcherSettingsFragmentClass.hookMethod("onCreatePreferences").runAfter { param ->
            if (!entryInLauncher) return@runAfter

            val preferenceScreen = param.thisObject.callMethod("getPreferenceScreen")
            val launchIntent: Intent =
                mContext.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
                    ?: return@runAfter
            // Root screen only; sub-screens pass a non-null preference root key
            if (param.args.getOrNull(1) != null) return@runAfter

            // android.R.attr.preferenceStyle resolves the framework layout, which the
            // androidx view holder cannot bind
            val themedContext =
                param.thisObject.callMethodSilently("getActivity") as? Context
                    ?: preferenceScreen.callMethodSilently("getContext") as? Context
                    ?: mContext

            val myPreference =
                if (preferenceStyledConstructor != null) {
                    preferenceStyledConstructor.newInstance(themedContext, null)
                } else {
                    preferenceClass
                        .getDeclaredConstructor(
                            Context::class.java,
                            AttributeSet::class.java,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                        ).newInstance(
                            themedContext,
                            null,
                            themedContext.resources.getIdentifier(
                                "preferenceStyle",
                                "attr",
                                mContext.packageName,
                            ),
                            0,
                        )
                }

            if (myPreference.hasMethod("setKey", String::class.java)) {
                myPreference.callMethod("setKey", BuildConfig.APPLICATION_ID)
            } else {
                myPreference.setField("mKey", BuildConfig.APPLICATION_ID)
            }
            myPreference.callMethod("setTitle", modRes.getString(R.string.app_name_shortened))
            myPreference.callMethod("setSummary", modRes.getString(R.string.app_motto))

            if (mContext.packageName == LAUNCHER3_PACKAGE) {
                myPreference.callMethod(
                    "setIcon",
                    modRes.getDrawable(R.drawable.ic_launcher_foreground),
                )

                val layoutResource =
                    mContext.resources.getIdentifier(
                        "settings_layout",
                        "layout",
                        mContext.packageName,
                    )
                if (layoutResource != 0) {
                    myPreference.callMethod("setLayoutResource", layoutResource)
                }
            }

            val listener =
                Proxy.newProxyInstance(
                    preferenceClass.classLoader,
                    arrayOf(preferenceClickListenerClass),
                ) { _, _, _ ->
                    mContext.startActivity(launchIntent)
                    true
                }

            if (myPreference.hasMethod("setOnPreferenceClickListener")) {
                myPreference.callMethod("setOnPreferenceClickListener", listener)
            } else if (preferenceClickListenerFieldName != null) {
                myPreference.setField(preferenceClickListenerFieldName, listener)
            } else {
                log(
                    this@LauncherSettings,
                    "No supported method found for preferenceClickListener.",
                )
            }

            preferenceScreen.callMethod("addPreference", myPreference)

            myPreference.javaClass
                .hookMethod("onBindViewHolder")
                .runBefore { param ->
                    val mKey = param.thisObject.getFieldSilently("mKey") as? String

                    if (mKey == BuildConfig.APPLICATION_ID) {
                        param.thisObject.setField("mAllowDividerAbove", false)
                        param.thisObject.setField("mAllowDividerBelow", false)
                    }
                }.runAfter { param ->
                    val holder = param.args[0]
                    val itemView = holder.getField("itemView") as View
                    val mKey = param.thisObject.getFieldSilently("mKey") as? String
                    val selectableBackground =
                        TypedValue()
                            .apply {
                                mContext.theme.resolveAttribute(
                                    android.R.attr.selectableItemBackground,
                                    this,
                                    true,
                                )
                            }.resourceId

                    if (mKey == BuildConfig.APPLICATION_ID) {
                        itemView.setBackgroundResource(selectableBackground)
                    }
                }
        }

        hookOptionsPopupEntry()
    }

    private fun hookOptionsPopupEntry() {
        val optionItemClass =
            findClass(
                $$"com.android.launcher3.views.OptionsPopupView$OptionItem",
                suppressError = true,
            )

        if (optionItemClass != null) {
            hookLegacyOptionsPopup(optionItemClass)
        } else {
            hookWorkspaceLongPressOptions()
        }
    }

    @Suppress("deprecation")
    private fun hookLegacyOptionsPopup(optionItemClass: Class<*>) {
        val optionsPopupViewClass =
            findClass("com.android.launcher3.views.OptionsPopupView", suppressError = true)
        val launcherEventEnum =
            findClass(
                $$"com.android.launcher3.logging.StatsLogManager$LauncherEvent",
                suppressError = true,
            ) ?: return
        val eventEnum =
            findClass(
                $$"com.android.launcher3.logging.StatsLogManager$EventEnum",
                suppressError = true,
            ) ?: return
        val optionItemConstructors = optionItemClass.declaredConstructors

        optionItemClass.hookConstructor().runBefore { param ->
            if (!entryInPopup) return@runBefore

            if (param.args[0] is Context) {
                val context = param.args[0] as Context
                val labelRes = param.args[1] as Int
                val iconRes = param.args[2] as Int
                val eventId = param.args[3]
                val clickListener = param.args[4]

                when (labelRes) {
                    -1 if iconRes == -1 -> {
                        param.thisObject.apply {
                            setField("labelRes", labelRes)
                            setField("label", modRes.getString(R.string.app_name_shortened))
                            setField(
                                "icon",
                                modRes.getDrawable(R.drawable.ic_launcher_foreground),
                            )
                            setField("eventId", eventId)
                            setField("clickListener", clickListener)
                        }
                    }

                    else -> {
                        param.thisObject.apply {
                            setField("labelRes", labelRes)
                            setField("label", context.getString(labelRes))
                            setField("icon", context.getDrawable(iconRes))
                            setField("eventId", eventId)
                            setField("clickListener", clickListener)
                        }
                    }
                }
            } else {
                val label = param.args[0] as CharSequence
                val icon = param.args[1] as Drawable
                val eventId = param.args[2]
                val clickListener = param.args[3]

                param.thisObject.apply {
                    setField("labelRes", 0)
                    setField("label", label)
                    setField("icon", icon)
                    setField("eventId", eventId)
                    setField("clickListener", clickListener)
                }
            }

            param.result = null
        }

        @Suppress("UNCHECKED_CAST")
        optionsPopupViewClass
            .hookMethod("getOptions")
            .runAfter { param ->
                if (!entryInPopup) return@runAfter

                val launcher = param.args[0]
                val options = param.result as ArrayList<Any>

                val eventId =
                    launcherEventEnum.enumConstants?.let {
                        Arrays
                            .stream(it)
                            .filter { c: Any -> c.toString() == "LAUNCHER_SETTINGS_BUTTON_TAP_OR_LONGPRESS" }
                            .findFirst()
                            .get()
                    }!!

                val clickListener =
                    object : View.OnLongClickListener {
                        override fun onLongClick(p0: View?): Boolean {
                            val launchIntent: Intent =
                                mContext.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
                                    ?: return false
                            mContext.startActivity(launchIntent)
                            return true
                        }
                    }

                val optionItem =
                    when {
                        optionItemConstructors.any {
                            it.parameterTypes.contentEquals(
                                arrayOf(
                                    CharSequence::class.java,
                                    Drawable::class.java,
                                    eventEnum,
                                    View.OnLongClickListener::class.java,
                                ),
                            )
                        } -> {
                            optionItemClass
                                .getDeclaredConstructor(
                                    CharSequence::class.java,
                                    Drawable::class.java,
                                    eventEnum,
                                    View.OnLongClickListener::class.java,
                                ).newInstance(
                                    modRes.getString(R.string.app_name_shortened),
                                    modRes.getDrawable(R.drawable.ic_launcher_foreground),
                                    eventId,
                                    clickListener,
                                )
                        }

                        optionItemConstructors.any {
                            it.parameterTypes.contentEquals(
                                arrayOf(
                                    Context::class.java,
                                    Int::class.javaPrimitiveType,
                                    Int::class.javaPrimitiveType,
                                    eventEnum,
                                    View.OnLongClickListener::class.java,
                                ),
                            )
                        } -> {
                            optionItemClass
                                .getDeclaredConstructor(
                                    Context::class.java,
                                    Int::class.javaPrimitiveType,
                                    Int::class.javaPrimitiveType,
                                    eventEnum,
                                    View.OnLongClickListener::class.java,
                                ).newInstance(
                                    launcher,
                                    -1,
                                    -1,
                                    eventId,
                                    clickListener,
                                )
                        }

                        else -> {
                            log("No supported constructor found for optionItemClass.")
                            null
                        }
                    }
                if (optionItem != null) {
                    options.add(optionItem)
                }

                param.result = options
            }
    }

    private fun hookWorkspaceLongPressOptions() {
        val workspaceLongPressOptionsClass =
            findClass("com.android.launcher3.popup.WorkspaceLongPressOptions") ?: return
        val popupDataClass = findClass("com.android.launcher3.popup.PopupData") ?: return
        val fixedStringClass =
            findClass($$"com.android.launcher3.popup.ui.StringContainer$FixedString") ?: return
        val popupDataConstructor =
            popupDataClass.declaredConstructors.maxByOrNull { it.parameterTypes.size } ?: return

        if (popupDataConstructor.parameterTypes.size != 7) {
            log(this@LauncherSettings, "Unexpected PopupData constructor arity.")
            return
        }

        val actionType = popupDataConstructor.parameterTypes[6]

        workspaceLongPressOptionsClass.hookMethod("getAll").runAfter { param ->
            if (!entryInPopup) return@runAfter

            @Suppress("UNCHECKED_CAST")
            val options = param.result as? MutableList<Any> ?: return@runAfter
            val template = options.lastOrNull() ?: return@runAfter

            val iconResId =
                mContext.resources
                    .getIdentifier("ic_setting", "drawable", mContext.packageName)
                    .takeIf { it != 0 }
                    ?: template.getField("iconResId") as Int

            val action =
                Proxy.newProxyInstance(
                    popupDataClass.classLoader,
                    arrayOf(actionType),
                ) { proxy, method, args ->
                    when (method.name) {
                        "hashCode" -> System.identityHashCode(proxy)
                        "equals" -> proxy === args?.firstOrNull()
                        "toString" -> "PLELiteSettingsAction"
                        else -> {
                            mContext.packageManager
                                .getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
                                ?.let { mContext.startActivity(it) }
                            null
                        }
                    }
                }

            options.add(
                popupDataConstructor.newInstance(
                    iconResId,
                    iconResId,
                    fixedStringClass
                        .getDeclaredConstructor(String::class.java)
                        .newInstance(modRes.getString(R.string.app_name_shortened)),
                    template.getField("category"),
                    template.getField("eventId"),
                    "",
                    action,
                ),
            )
        }
    }
}
