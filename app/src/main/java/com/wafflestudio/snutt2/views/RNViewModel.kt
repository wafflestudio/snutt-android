package com.wafflestudio.snutt2.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class RNViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
) : AndroidViewModel(application) {

    private val _done = MutableLiveData(false)
    val done: LiveData<Boolean> get() = _done

    val token get() = userRepository.accessToken.value

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bundleFile = File(application.applicationContext.cacheDir, "android.jsbundle")

                val url = if (userRepository.rnConfig.value) {
                    "http://localhost:8081/index.bundle?platform=android"
                } else {
                    userRepository.rnUrl()
                }
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.connect()
                val inputStream = urlConnection.inputStream

                val outputStream = FileOutputStream(bundleFile)
                val buffer = ByteArray(1024000)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
                inputStream.close()
                urlConnection.disconnect()
                _done.postValue(true)
            } catch (e: Exception) {
            }
        }
    }
}
