package com.mk.develop.simpleandroidview

import android.app.Activity
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun SimpleAndroidView(activity: Activity, policy: Boolean = false, url: String, navigateToGameCallback: () -> Unit, onPageFinishedLoggerEvent: ((userAgentString: String) -> Unit)) {
    WebConfig.fullScreenForWeb(activity)
    var webView: WebView? by remember { mutableStateOf(null) }
    val viewClient = offerWebViewClient(
        navigateToGameCallback = { navigateToGameCallback() },
        onPageFinishedLoggerEvent = { onPageFinishedLoggerEvent(it) }
    )
    val chromeClient = offerWebChromeClient()

    AndroidView(factory = {
        WebView(it).apply {
            WebConfig.webViewSettings(this)
            this.webViewClient = viewClient
            this.webChromeClient = chromeClient
            webView = this
            this.loadUrl(url)
        }
    })

    BackHandler(enabled = true) {
        if (policy) navigateToGameCallback()
        else webView?.let { WebConfig.onBackPressedEvent(it) }
    }
}
