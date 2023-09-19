package com.mk.develop.simpleandroidview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.startActivity
import com.mk.develop.simpleandroidview.utils.AppConst

@Composable
internal fun offerWebViewClient(
    navigateToGameCallback: (() -> Unit)?,
    onPageFinishedLoggerEvent: ((userAgentString: String) -> Unit)?
) : WebViewClient {

    val accompanistWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            onPageFinishedLoggerEvent?.let { it(view.settings.userAgentString) }
            if (view.title?.contains(AppConst.MODERATION_KEY) == true) {
                navigateToGameCallback?.let { it() }
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString() ?: return false
            return try {
                when {
                    url.startsWith(AppConst.HTTP) || url.startsWith(AppConst.HTTPS) -> return false
                    else -> {
                        when {
                            url.startsWith(AppConst.MAILTO) -> {
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse(url)
                                    view?.context?.startActivity(this)
                                }
                            }

                            url.startsWith("fb://") -> {
                                try {
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                        view?.context?.startActivity(this)
                                    }
                                } catch (e: ActivityNotFoundException) {

                                }
                            }

                            url.startsWith(AppConst.TEL) -> {
                                Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse(url)
                                    view?.context?.startActivity(this)
                                }
                            }

                            url.startsWith(AppConst.TELEG) -> {
                                val telegramUrl = url.replace(AppConst.TELEG, AppConst.HTTP_TELEG)
                                Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl)).apply {
                                    data = Uri.parse(url)
                                    view?.context?.startActivity(this)
                                }
                            }
                        }
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            view?.context?.startActivity(this)
                        }
                        return true
                    }
                }
            } catch (_: Exception) {
                true
            }
        }
    }
    return accompanistWebViewClient
}