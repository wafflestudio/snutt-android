package com.wafflestudio.snutt2.components.compose.embed_map

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    fun moveToMapApplication(context: Context, building: LectureBuildingDto) {
        try {
            openNaverMap(context, building)
        } catch (e: Exception) {
            try {
                openKakaoMap(context, building)
            } catch (e: Exception) {
                context.toast(context.getString(R.string.embed_map_cannot_open_url_scheme))
            }
        }
    }

    private fun openNaverMap(context: Context, building: LectureBuildingDto) {
        context.startActivity(
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
            ),
        )
    }

    private fun openKakaoMap(context: Context, building: LectureBuildingDto) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("kakaomap://look?p=${building.locationInDMS.latitude},${building.locationInDMS.longitude}"),
            ),
        )
    }
}
