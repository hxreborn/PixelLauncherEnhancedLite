package com.drdisagree.pixellauncherenhanced.data.provider

import com.crossbowffs.remotepreferences.RemotePreferenceFile
import com.crossbowffs.remotepreferences.RemotePreferenceProvider
import com.drdisagree.pixellauncherenhanced.BuildConfig
import com.drdisagree.pixellauncherenhanced.data.common.Constants.SHARED_PREFERENCES

class RemotePrefProvider :
    RemotePreferenceProvider(
        BuildConfig.APPLICATION_ID,
        arrayOf(RemotePreferenceFile(SHARED_PREFERENCES, true)),
    )
