package ua.`in`.asilichenko.shortedges.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.Response
import ua.`in`.asilichenko.shortedges.data.FileServerApi
import ua.`in`.asilichenko.shortedges.data.model.ApiErrorResponse
import ua.`in`.asilichenko.shortedges.util.saveImageToInternalStorage
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val fileServerApi: FileServerApi,
    @ApplicationContext private val context: Context
) {
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun fetchAndSaveImage(imageId: Int): Boolean {
        val response = fileServerApi.getImage(imageId)

        if (response.isSuccessful && response.body() != null) {
            val inputStream = response.body()?.byteStream()

            inputStream?.let { input ->
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "imp.png")
                    put(MediaStore.Downloads.MIME_TYPE, "image/png")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { fileUri ->
                    resolver.openOutputStream(fileUri)?.use { output ->
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                        }
                        output.flush()
                    }
                }
            }
            return true

        } else {
            Log.e("ImageTest", "Failed to download image: ${response.code()}")
            return false

        }
    }
}
