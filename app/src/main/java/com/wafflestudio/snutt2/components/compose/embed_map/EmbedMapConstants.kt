package com.wafflestudio.snutt2.components.compose.embed_map

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate

object EmbedMapConstants {
    const val SinglePinMapZoom = 14.3
    const val BaseLongitude = 126.9527
}

object EmbedMapUtils {
    fun getCameraUpdateFromLatLngList(
        points: List<LatLng>,
    ): CameraUpdate {
        points.distinct().let { latLngs ->
            return if (latLngs.isEmpty()) {
                CameraUpdate.toCameraPosition(CameraPosition.INVALID)
            } else if (latLngs.size == 1) {
                CameraUpdate.scrollAndZoomTo(
                    LatLng(
                        latLngs.first().latitude,
                        EmbedMapConstants.BaseLongitude,
                    ),
                    EmbedMapConstants.SinglePinMapZoom,
                )
            } else {
                CameraUpdate.scrollAndZoomTo(
                    LatLng(
                        latLngs.sumOf { it.latitude } / latLngs.size,
                        EmbedMapConstants.BaseLongitude,
                    ),
                    EmbedMapConstants.SinglePinMapZoom,
                )
            }
        }
    }
}
