package com.mk.develop.simpleandroidview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.core.content.FileProvider
import com.mk.develop.simpleandroidview.utils.AppConst
import com.mk.develop.simpleandroidview.utils.decrypt
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun showFileChooserPhoto(
    cameraIntent: Intent?,
): Intent {
    val galleryIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = AppConst.IMAGE_PATH
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
    val chooserIntent = Intent(Intent.ACTION_CHOOSER).run {
        putExtra(Intent.EXTRA_INTENT, galleryIntent)
        putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            cameraIntent?.let { arrayOf(it) } ?: arrayOfNulls(0))
    }

    return chooserIntent
}

fun getCameraIntent(
    context: Context,
    imageUri: MutableState<Uri?>,
): Intent? {
    val time: String = SimpleDateFormat("eXl5eU1NZGRfSEhtbXNz".decrypt(), Locale.getDefault()).format(Date())
    val storage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("${"UEhPVE9fSlBFR18=".decrypt()}${time}_", "LmpwZw==".decrypt(), storage)
    val authority = context.packageName + AppConst.IMAGE_PACKAGE_NAME
    val uri = FileProvider.getUriForFile(
        context,
        authority,
        file,
    )
    imageUri.value = uri
    return try {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
    } catch (_: Exception) {
        null
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = context.openFileOutput("game_user_image.jpg", Context.MODE_PRIVATE)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}

fun loadImageFromInternalStorage(context: Context): Bitmap? {
    return try {
        val inputStream = context.openFileInput("game_user_image.jpg")
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: FileNotFoundException) {
        null
    }
}