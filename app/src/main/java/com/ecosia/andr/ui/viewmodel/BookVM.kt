package com.ecosia.andr.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.ecosia.andr.R
import com.ecosia.andr.util.TagSender
import com.ecosia.andr.util.UriBuilder
import com.facebook.applinks.AppLinkData

class BookVM(application: Application) : AndroidViewModel(application) {
    private var isAppsSuccess: Boolean = false
    private val builder = UriBuilder()
    private val sender = TagSender()
    private val TAG = "customTagViewModel"

    val urlLiveData: MutableLiveData<String> = MutableLiveData()

    fun getDeepLink(activity: Activity) {
        Log.d(TAG, "in deep")
        val sharedPref = activity.getSharedPreferences(
            activity.getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )
        isAppsSuccess = sharedPref.getBoolean("appsSuccess", false)
        Log.d(TAG, "isAppsSuccess is $isAppsSuccess")
        AppLinkData.fetchDeferredAppLinkData(activity) {
            val deepLink = it?.targetUri.toString()
            if (deepLink == "null") {
                Log.d(TAG, "checked deep")
                if (!isAppsSuccess) {
                    getApps(activity)
                    Log.d(TAG, "checked if apps was started already")
                }
            } else {
                urlLiveData.postValue(builder.createUrl(deepLink, null, activity))
                sender.sendTag(deepLink, null)
            }
        }
    }

    private fun getApps(activity: Activity) {
        Log.d(TAG, "started apps")
        AppsFlyerLib.getInstance()
            .init(activity.getString(R.string.apps_dev_key), object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                    urlLiveData.postValue(builder.createUrl("null", data, activity))
                    sender.sendTag("null", data)
                    val sharedPref = activity.getSharedPreferences(
                        activity.getString(R.string.shared_pref_name),
                        Context.MODE_PRIVATE
                    )
                    sharedPref.edit {
                        putBoolean("appsSuccess", true)
                        apply()
                    }

                    Log.d(TAG, "apps success")
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(TAG, "apps fail")
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    TODO("Not yet implemented")
                }

                override fun onAttributionFailure(p0: String?) {
                    TODO("Not yet implemented")
                }

            }, activity)
        AppsFlyerLib.getInstance().start(activity)
    }
}