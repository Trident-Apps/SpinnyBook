package com.ecosia.andr.util

import android.app.Activity
import android.provider.Settings
import java.io.File

class Checker {
    fun isDeviceSecured(activity: Activity): Boolean {
        return checkRoot() || checkADB(activity) == "1"
    }

    private fun checkADB(activity: Activity): String {
        return Settings.Global.getString(activity.contentResolver, Settings.Global.ADB_ENABLED)
            ?: "null"
    }

    private fun checkRoot(): Boolean {
        val dirs = arrayOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
        )
        try {
            for (dir in dirs) {
                if (File(dir + "su").exists()) return true
            }
        } catch (t: Throwable) {
        }
        return false
    }
}