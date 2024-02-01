package com.wafflestudio.snutt2.components.compose.embed_map

import androidx.compose.ui.unit.dp
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.overlay.OverlayImage
import com.wafflestudio.snutt2.R

object EmbedMapConstants {
    const val SinglePinMapZoom = 14.3
    const val BaseLongitude = 126.9527
    val MapShortHeight = 255.dp
    val MapLongHeight = 350.dp
    val DefaultMapUISettings = MapUiSettings(
        isLogoClickEnabled = false,
        isZoomControlEnabled = false,
        isCompassEnabled = false,
        isIndoorLevelPickerEnabled = false,
        isLocationButtonEnabled = false,
        isRotateGesturesEnabled = false,
        isScrollGesturesEnabled = false,
        isStopGesturesEnabled = false,
        isTiltGesturesEnabled = false,
        isZoomGesturesEnabled = false,
        isScaleBarEnabled = false,
    )
    val normalMarker = OverlayImage.fromResource(
        R.drawable.ic_map_pin,
    )
    val DimBoundary = listOf(
        LatLng(38.0, 126.0),
        LatLng(38.0, 127.0),
        LatLng(37.0, 127.0),
        LatLng(37.0, 126.0),
    )
}
