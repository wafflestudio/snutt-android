package com.wafflestudio.snutt2.lib.network
//
//import android.net.ConnectivityManager
//import android.net.Network
//import android.net.NetworkCapabilities
//import android.net.NetworkRequest
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.filterNotNull
//import kotlinx.coroutines.flow.shareIn
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class NetworkConnectivityManager @Inject constructor(
//    connectivityManager: ConnectivityManager,
//) {
//    private val _networkConnectivity = MutableStateFlow<Boolean?>(null)
//    val networkConnectivity = _networkConnectivity.filterNotNull()
//        .shareIn(
//            CoroutineScope(Dispatchers.Main),
//            SharingStarted.Eagerly,
//            replay = 1,
//        )
//
//    init {
//        connectivityManager.registerNetworkCallback(
//            NetworkRequest.Builder().apply {
//                addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//                addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            }.build(),
//            object : ConnectivityManager.NetworkCallback() {
//                override fun onLost(network: Network) {
//                    super.onLost(network)
//                    _networkConnectivity.value = false
//                }
//
//                override fun onAvailable(network: Network) {
//                    super.onAvailable(network)
//                    _networkConnectivity.value = true
//                }
//            },
//        )
//    }
//}
