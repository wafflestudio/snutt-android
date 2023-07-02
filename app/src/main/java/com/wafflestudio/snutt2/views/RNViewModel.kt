package com.wafflestudio.snutt2.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
    application: Application
) : AndroidViewModel(application) {

    private val _done = MutableLiveData(false)
    val done: LiveData<Boolean> get() = _done

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val urlConnection = URL("https://snutt-rn-assets.s3.ap-northeast-2.amazonaws.com/android.jsbundle").openConnection() as HttpURLConnection
            urlConnection.connect()
            val inputStream = urlConnection.inputStream
            val outputFile = File(application.applicationContext.cacheDir, "android.jsbundle")

            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024000)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.close()
            inputStream.close()
            urlConnection.disconnect()
            _done.postValue(true)
        }
    }
}
