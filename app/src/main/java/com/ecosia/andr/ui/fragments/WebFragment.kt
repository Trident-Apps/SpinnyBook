package com.ecosia.andr.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ecosia.andr.R
import com.ecosia.andr.databinding.WebFragmentLayoutBinding

class WebFragment : Fragment() {
    private lateinit var binding: WebFragmentLayoutBinding
    private lateinit var webView: WebView
    private var isRedirected: Boolean = true
    private var messageAb: ValueCallback<Array<Uri?>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.web_fragment_layout, container, false)

        webView = binding.webView
        webView.loadUrl(arguments?.getString(ARGUMENTS_KEY) ?: "url not passed")
        Log.d("customTagWeb", arguments?.getString("url").toString())
        webView.webViewClient = LocalClient()
        webView.settings.userAgentString = System.getProperty(USER_AGENT)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                selectImageIfNeed()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.webChromeClient = this
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view!!.loadUrl(url!!)
                        isRedirected = true
                        return true
                    }
                }
                return true
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (webView.canGoBack()) {
//                        webView.goBack()
//                    } else {
//                        isEnabled = false
//                    }
//                }
//            })

        webView.canGoBack()
        webView.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action == MotionEvent.ACTION_UP && webView.canGoBack()
            ) {
                webView.goBack()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            messageAb?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (messageAb == null) return

            messageAb!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            messageAb = null
        }
    }

    private fun selectImageIfNeed() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = INTENT_TYPE
        startActivityForResult(
            Intent.createChooser(intent, CHOOSER_TITLE), RESULT_CODE
        )
    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            isRedirected = false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (!isRedirected) {
                url?.let { url ->
                    if (url == BASE_URL) {
                        findNavController().navigate(R.id.startGameFragment)
                    } else {
                        val sharedPref = requireActivity().getSharedPreferences(
                            requireActivity().getString(R.string.shared_pref_name),
                            Context.MODE_PRIVATE
                        )
                        sharedPref.edit().putString("savedUrl", url).apply()
                        Log.d("customTagWeb", "saved url is $url ")
                    }
                }
            }
        }
    }

    companion object {
        const val ARGUMENTS_KEY = "url"
        const val USER_AGENT = "http.agent"
        const val INTENT_TYPE = "image/*"
        const val CHOOSER_TITLE = "Image Chooser"
        const val BASE_URL = "https://spinnybook.site/"
        const val RESULT_CODE = 1
    }
}