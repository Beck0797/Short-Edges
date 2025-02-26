package ua.`in`.asilichenko.shortedges.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ua.`in`.asilichenko.shortedges.data.model.ApiErrorResponse


interface FileServerApi {

    companion object{
        const val BASE_URL = "http://192.168.0.10:15000"
        const val CLIENT_ID = ""
    }

    @GET("get-image/{image_id}")
    suspend fun getImage(
        @Path("image_id") imageId: Int
    ): Response<ResponseBody> // If you want to map the error response

}