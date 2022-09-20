package com.ecosia.andr.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
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

    val urlLiveData: MutableLiveData<String> = MutableLiveData()

    fun getDeepLink(activity: Activity) {
        val sharedPref = activity.getSharedPreferences(
            activity.getString(R.string.shared_pref_name),
            Context.MODE_PRIVATE
        )
        isAppsSuccess = sharedPref.getBoolean("appsSuccess", false)
        AppLinkData.fetchDeferredAppLinkData(activity) {
            val deepLink = it?.targetUri.toString()
            if (deepLink == "null") {
                if (!isAppsSuccess) {
                    getApps(activity)
                }
            } else {
                urlLiveData.postValue(builder.createUrl(deepLink, null, activity))
                sender.sendTag(deepLink, null)
            }
        }
    }

    private fun getApps(activity: Activity) {
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
                }

                override fun onConversionDataFail(data: String?) {}

                override fun onAppOpenAttribution(data: MutableMap<String, String>?) {}

                override fun onAttributionFailure(data: String?) {}

            }, activity)
        AppsFlyerLib.getInstance().start(activity)
    }
}