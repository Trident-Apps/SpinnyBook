package com.ecosia.andr.util

import android.content.Context
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerLib
import com.ecosia.andr.R
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.util.*

class UriBuilder() {
    fun createUrl(
        deepLink: String,
        data: MutableMap<String, Any>?,
        activity: Context?
    ): String {
        val gadId =
            AdvertisingIdClient.getAdvertisingIdInfo(activity?.applicationContext!!).id.toString()
        val url = activity.getString(R.string.base_url).toUri().buildUpon().apply {
            appendQueryParameter(
                activity.getString(R.string.secure_get_parametr),
                activity.getString(R.string.secure_key)
            )
            appendQueryParameter(activity.getString(R.string.dev_tmz_key), TimeZone.getDefault().id)
            appendQueryParameter(activity.getString(R.string.gadid_key), gadId)
            appendQueryParameter(activity.getString(R.string.deeplink_key), deepLink)
            appendQueryParameter(
                activity.getString(R.string.source_key),
                data?.get("media_source").toString()
            )
            if (deepLink == "null") {
                appendQueryParameter(
                    activity.getString(R.string.source_key),
                    data?.get("media_source").toString()
                )
            } else {
                appendQueryParameter(activity.getString(R.string.source_key), "deeplink")
            }
            if (deepLink == "null") {
                appendQueryParameter(
                    activity.getString(R.string.af_id_key),
                    AppsFlyerLib.getInstance().getAppsFlyerUID(activity.applicationContext)
                )
            } else {
                appendQueryParameter(activity.getString(R.string.af_id_key), "null")
            }
            appendQueryParameter(
                activity.getString(R.string.adset_id_key),
                data?.get(DATA_ADSET_ID).toString()
            )
            appendQueryParameter(
                activity.getString(R.string.campaign_id_key),
                data?.get(DATA_CAMPAIGN_ID).toString()
            )
            appendQueryParameter(
                activity.getString(R.string.app_campaign_key),
                data?.get(DATA_CAMPAIGN).toString()
            )
            appendQueryParameter(
                activity.getString(R.string.adset_key),
                data?.get(DATA_ADSET).toString()
            )
            appendQueryParameter(
                activity.getString(R.string.adgroup_key),
                data?.get(DATA_ADGROUP).toString()
            )
            appendQueryParameter(
                activity.getString(R.string.orig_cost_key),
                data?.get(DATA_ORIG_COST).toString()
            )
            appendQueryParameter(
                activity.getString(R.string.af_siteid_key),
                data?.get(DATA_AF_SITEID).toString()
            )

        }.toString()
        return url
    }

    companion object {
        val DATA_ADSET_ID = "adset_id"
        val DATA_CAMPAIGN_ID = "campaign_id"
        val DATA_CAMPAIGN = "campaign"
        val DATA_ADSET = "adset"
        val DATA_ADGROUP = "adgroup"
        val DATA_ORIG_COST = "orig_cost"
        val DATA_AF_SITEID = "af_siteid"
    }
}