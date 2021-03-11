package com.chenyue404.nojump

import com.crossbowffs.remotepreferences.RemotePreferenceProvider

class MyPreferenceProvider :
    RemotePreferenceProvider("com.chenyue404.noiump.preferences", arrayOf(PREF_NAME)) {
    companion object {
        final val PREF_NAME = "main_prefs"
        final val KEY_NAME = "main_prefs_key"
    }
}