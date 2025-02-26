package ua.`in`.asilichenko.shortedges.viewmodel

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.`in`.asilichenko.shortedges.data.PreferenceManager
import ua.`in`.asilichenko.shortedges.data.UdpClient
import ua.`in`.asilichenko.shortedges.repository.MainRepository
import ua.`in`.asilichenko.shortedges.util.ImageToStringConverter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val preferenceManager: PreferenceManager,
    private val udpClient: UdpClient
) : ViewModel() {


    private val _imageFetchResult = MutableLiveData<Boolean>()
    val imageFetchResult: LiveData<Boolean> get() = _imageFetchResult

    private val _receivedMessage = MutableLiveData<String>()
    val receivedMessage: LiveData<String> get() = _receivedMessage
    private val imageConverter : ImageToStringConverter = ImageToStringConverter()

    fun sendReadyCommand(ipAddress: String) {
        viewModelScope.launch {
            val index = extractLastDigits(ipAddress)
            if (index < 10) {
                sendUdpMessage("RM_0$index")
            } else {
                sendUdpMessage("RM_10")
            }
        }
    }

    fun sendStateMessage(ipAddress: String) {
        viewModelScope.launch {
            val index = extractLastDigits(ipAddress)
            if (index < 10) {
                sendUdpMessage("ST_0$index")
            } else {
                sendUdpMessage("ST_10")
            }
        }
    }

    fun fetchImage(pAddress: String) {
        val index = extractLastDigits(pAddress)
        viewModelScope.launch {
            val result = mainRepository.fetchAndSaveImage(index)
            _imageFetchResult.postValue(result) // Update LiveData
        }
    }

    fun extractLastDigits(ip: String): Int {
        // Split the IP string by dots
        val parts = ip.split(".")

        // Ensure we have 4 parts in the IP address
        if (parts.size != 4) return -1

        // Get the last part (last two digits)
        val lastPart = parts[3]

        // Remove leading zeros and convert to an integer
        var num = lastPart.last().toString().toInt()

        if (num == 0) {
            num = 10
        }

        return num
    }

    fun startUdpClient() {
        udpClient.start { message ->
            Log.e("ImageTest", "UDP recv: $message")

            _receivedMessage.postValue(message) // Update LiveData on the main thread
        }
    }

    private fun sendUdpMessage(message: String) {
        Log.e("ImageTest", "UPD send: $message")
        viewModelScope.launch {
            udpClient.sendMessage(message)
        }
    }

    fun saveUserImage(bitmap: Bitmap) {
        val stringImage = imageConverter.convertImageToString(bitmap)
        preferenceManager.write(PreferenceManager.USER_IMAGE, stringImage)
    }

    fun delUserImage() {
        preferenceManager.write(PreferenceManager.USER_IMAGE, "")
    }

    fun getUserImage(): Bitmap? {
        val stringImage = preferenceManager.read(PreferenceManager.USER_IMAGE, "")
        return imageConverter.convertStringToBitMap(stringImage)
    }

}