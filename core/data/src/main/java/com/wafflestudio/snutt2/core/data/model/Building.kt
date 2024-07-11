package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Building
import com.wafflestudio.snutt2.core.model.data.Campus
import com.wafflestudio.snutt2.core.model.data.GeoCoordinate
import com.wafflestudio.snutt2.core.network.model.GeoCoordinate as GeoCoordinateNetwork
import com.wafflestudio.snutt2.core.network.model.Campus as CampusNetwork
import com.wafflestudio.snutt2.core.network.model.LectureBuildingDto

fun LectureBuildingDto.toExternalModel() = Building(
    campus = this.campus.toExternalModel(),
    buildingNumber = this.buildingNumber,
    buildingNameKor = this.buildingNameKor ?: "",
    buildingNameEng = this.buildingNameEng ?: "",
    coordinate = this.locationInDMS.toExternalModel(),
)

fun CampusNetwork.toExternalModel(): Campus {
    return when (this) {
        CampusNetwork.GWANAK -> Campus.GWANAK
        CampusNetwork.YEONGEON -> Campus.YEONGEON
        CampusNetwork.PYEONGCHANG -> Campus.PYEONGCHANG
    }
}

fun GeoCoordinateNetwork.toExternalModel() = GeoCoordinate(
    latitude = this.latitude,
    longitude = this.longitude,
)