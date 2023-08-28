package com.mk.develop.simpleandroidview

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import com.mk.develop.simpleandroidview.utils.AppConst.AGENT_STRING

internal object WebConfig {
    private var backPressTime: Long = 0
    fun onBackPressedEvent(webView: WebView) {
        val currentIndex = webView.copyBackForwardList().currentIndex
        if (webView.canGoBackOrForward(-currentIndex)) {
            if ((System.currentTimeMillis() - backPressTime) < 2000) {
                webView.goBackOrForward(-currentIndex)
            } else webView.goBackOrForward(-1)
            backPressTime = System.currentTimeMillis()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun webViewSettings(webView: WebView) {
        webView.apply {
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            isSaveEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                importantForAutofill = WebView.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            }
        }
        webView.settings.apply {
            domStorageEnabled = true
            mixedContentMode = 0
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            databaseEnabled = true
            useWideViewPort = true
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            allowContentAccess = true
            setSupportMultipleWindows(false)
            builtInZoomControls = true
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = userAgentString.replace(AGENT_STRING, "")
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) saveFormData = true
        }
    }

    @Suppress("DEPRECATION")
    fun fullScreenForWeb(activity: Activity) {
        val decorView: View = activity.window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }
}