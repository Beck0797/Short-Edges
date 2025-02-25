package ua.`in`.asilichenko.shortedges.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

public fun saveImageToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    fileName: String
): String? {
    val directory = File(context.filesDir, "images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val imageFile = File(directory, fileName)
    try {
        FileOutputStream(imageFile).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            Log.d("ImageTest", "Image saved at: " + imageFile.absolutePath)
            return imageFile.absolutePath
        }
    } catch (e: IOException) {
        Log.e("ImageTest", "Error saving image: " + e.message, e)
    }
    return null
}