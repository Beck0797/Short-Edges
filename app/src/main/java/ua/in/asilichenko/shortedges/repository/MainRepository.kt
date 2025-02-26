package ua.`in`.asilichenko.shortedges.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import ua.`in`.asilichenko.shortedges.data.FileServerApi
import ua.`in`.asilichenko.shortedges.data.PreferenceManager
import ua.`in`.asilichenko.shortedges.data.model.ApiErrorResponse
import ua.`in`.asilichenko.shortedges.util.ImageToStringConverter
import ua.`in`.asilichenko.shortedges.util.saveImageToInternalStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val fileServerApi: FileServerApi,
    @ApplicationContext private val context: Context,
    private val preferenceManager: PreferenceManager
) {
    suspend fun fetchAndSaveImage(imageId: Int): Boolean {
        val response = fileServerApi.getImage(imageId)
        val imageConverter: ImageToStringConverter = ImageToStringConverter()

        return try {
            if (response.isSuccessful && response.body() != null) {
                val inputStream = response.body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val stringImage = imageConverter.convertImageToString(bitmap)
                preferenceManager.write(PreferenceManager.USER_IMAGE, stringImage);

                Log.e("ImageTest", "Downlaoded image: ${response.code()}")

                true

            } else {
                Log.e("ImageTest", "Failed to download image: ${response.code()}")
                false

            }
        } catch (e: Exception) {
            false
        }
    }
}
