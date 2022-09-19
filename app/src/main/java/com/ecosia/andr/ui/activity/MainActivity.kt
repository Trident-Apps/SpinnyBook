package com.ecosia.andr.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ecosia.andr.R
import com.ecosia.andr.databinding.ActivityMainBinding
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        lifecycleScope.launch(Dispatchers.IO) {

            val gadId =
                AdvertisingIdClient.getAdvertisingIdInfo(application.applicationContext).id.toString()
            OneSignal.initWithContext(application.applicationContext)
            OneSignal.setAppId(this@MainActivity.getString(R.string.onesignal_id))
            OneSignal.setExternalUserId(gadId)
        }
    }
}