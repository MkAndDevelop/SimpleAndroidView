package com.mk.develop.simpleandroidview

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
internal fun offerWebChromeClient(): WebChromeClient {
    var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    val context = LocalContext.current
    val imageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    lateinit var req: PermissionRequest
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) try {
                req.grant(req.resources)
            } catch (_: Exception) {
            }
        }
    )
    val startCameraForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = remember {
            {
                if (it.resultCode != Activity.RESULT_OK) {
                    imageUri.value = null
                    fileChooserCallback?.onReceiveValue(null)
                    fileChooserCallback = null
                }

                val data = it.data
                when {
                    data?.data != null -> {
                        data.data?.let { uri ->
                            fileChooserCallback?.onReceiveValue(arrayOf(uri))
                        }
                    }

                    data?.clipData != null -> {
                        val uriList = mutableListOf<Uri>()

                        data.clipData?.let { clipData ->
                            for (i in 0 until clipData.itemCount)
                                uriList.add(clipData.getItemAt(i).uri)
                        }
                        fileChooserCallback?.onReceiveValue(uriList.toTypedArray())
                    }

                    imageUri.value != null -> {
                        imageUri.value?.let { uri ->
                            fileChooserCallback?.onReceiveValue(arrayOf(uri))
                        }
                    }
                    else -> {
                        fileChooserCallback = null
                        imageUri.value = null
                    }
                }
                fileChooserCallback = null
                imageUri.value = null
            }
        }
    )
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            startCameraForResult.launch(
                showFileChooserPhoto(
                    if (it) getCameraIntent(context, imageUri) else null
                )
            )
        }
    )

    val webChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            fileChooserCallback = filePathCallback
            val permission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startCameraForResult.launch(
                    showFileChooserPhoto(
                        getCameraIntent(context, imageUri)
                    )
                )
            } else requestPermissionLauncher.launch(permission)
            return true
        }

        override fun onPermissionRequest(request: PermissionRequest?) {
            request?.resources?.forEach { resource ->
                if (resource.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                    req = request
                    val permission = Manifest.permission.CAMERA
                    if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(req.resources)
                    } else requestPermission.launch(permission)
                }
            }
        }
    }
    return webChromeClient
}

