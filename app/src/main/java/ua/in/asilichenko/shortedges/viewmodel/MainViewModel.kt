package ua.`in`.asilichenko.shortedges.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.`in`.asilichenko.shortedges.repository.MainRepository
import ua.`in`.asilichenko.shortedges.util.Resource
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _imageFetchResult = MutableLiveData<Boolean>()
    val imageFetchResult: LiveData<Boolean> get() = _imageFetchResult
    fun sendReadyCommand(ipAddress: String) {
        Log.e("ImageTest", "vm -> sendReadyCommand(): $ipAddress")
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

        if (num == 0){
            num = 10
        }

        return num
    }

}