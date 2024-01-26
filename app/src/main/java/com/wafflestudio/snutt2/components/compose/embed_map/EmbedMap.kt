package com.wafflestudio.snutt2.components.compose.embed_map

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerDefaults
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PolygonOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberMarkerState
import com.naver.maps.map.overlay.OverlayImage
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.MapPinBinding
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun EmbedMap(
    modifier: Modifier,
    distinctBuildings: List<LectureBuildingDto>,
) {
    val context = LocalContext.current

    /* 지도 dim */
    var mapDimmed by remember {
        mutableStateOf(false)
    }
    val symbolScale = animateFloatAsState(targetValue = if (mapDimmed) 0f else 1f, label = "")
    val dimAlpha by animateFloatAsState(targetValue = if (mapDimmed) 0.4f else 0f, label = "")

    /* 지도 카메라 */
    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(Unit) {
        cameraPositionState.move(
            EmbedMapUtils.getCameraUpdateFromLatLngList(
                distinctBuildings.map { it.locationInDMS.toLatLng() },
            ),
        )
    }

    NaverMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = { _, _ ->
            mapDimmed = mapDimmed.not()
        },
        properties = MapProperties(symbolScale = symbolScale.value),
        uiSettings = MapUiSettings(
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
        ),
    ) {
        distinctBuildings.forEach { building ->
            Marker(
                anchor = MarkerDefaults.Anchor,
                icon = if (mapDimmed) {
                    OverlayImage.fromView(
                        MapPinBinding.inflate(LayoutInflater.from(context)).root.also {
                            it.findViewById<TextView>(R.id.building_text).text =
                                "${building.buildingNameKor} 길찾기"
                        },
                    )
                } else {
                    OverlayImage.fromResource(
                        R.drawable.ic_map_pin,
                    )
                },
                captionText = building.buildingNameKor,
                state = rememberMarkerState(
                    position = CameraPosition(
                        building.locationInDMS.let { LatLng(it.latitude, it.longitude) },
                        6.0,
                    ).target,
                ),
                onClick = {
                    if (mapDimmed) {
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "nmap://place?lat=${building.locationInDMS.latitude}&lng=${building.locationInDMS.longitude}&name=${
                                URLEncoder.encode(
                                    building.buildingNameKor,
                                    StandardCharsets.UTF_8.toString(),
                                )
                                }&appname=com.wafflestudio.snutt2",
                            ),
                        ).apply {
                            context.startActivity(this)
                        }
                        mapDimmed = false
                        true
                    } else {
                        false
                    }
                },
            )
        }
        PolygonOverlay(
            coords = listOf(
                LatLng(38.0, 126.0),
                LatLng(38.0, 127.0),
                LatLng(37.0, 127.0),
                LatLng(37.0, 126.0),
            ),
            color = SNUTTColors.Black900.copy(alpha = dimAlpha),
        )
    }
}
