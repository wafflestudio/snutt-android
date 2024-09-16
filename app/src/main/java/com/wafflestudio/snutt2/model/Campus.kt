package com.wafflestudio.snutt2.model

import com.wafflestudio.snutt2.core.network.model.Campus as CampusNetwork

enum class Campus {
    GWANAK,
    YEONGEON,
    PYEONGCHANG,
}

fun CampusNetwork.toExternalModel(): Campus {
    return when (this) {
        CampusNetwork.GWANAK -> Campus.GWANAK
        CampusNetwork.YEONGEON -> Campus.YEONGEON
        CampusNetwork.PYEONGCHANG -> Campus.PYEONGCHANG
    }
}
