package com.wafflestudio.snutt2.core.network

import android.graphics.Color
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

//abstract class A(
//    open val a: String,
//)
//
//abstract class B(
//    override val a: String,
//    open val b: String,
//): A(a)
//
//data class C(
//    override val a: String,
//    override val b: String,
//): B(a,b)
//
////data class Lecture(
////    val id: String,
////    val courseTitle,
////    val credit,
////    val placeTimes,
////    val instructor,
////    val remark,
////)
////
////data class OriginalLecture(
////    override val id :
////    val registrationCount,
////    val wasFull,
////): Lecture
//
//fun main(){
//    val a:C = C("5", "4")
//}

enum class Campus {
    GWANAK,
    YEONGEON,
    PYEONGCHANG,
}

data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double,
)


data class Building(
    val campus: Campus,
    val buildingNumber: String, // 3-101 -> 동만 내려옴 ex: "7" (왜 7인지는 모르겠으나..)
    val buildingNameKor: String, // 인문3동 -> ex: "인문관6"
    val buildingNameEng: String, // Humanity 3?? 뭐지 -> ex: "College of Humanitites 6" 아직 안쓰긴 함
    val coordinate: GeoCoordinate // 위경도
)

data class Time (
    val timeInMinutes: Int
) {
    val minute: Int get() = timeInMinutes % 60
    val hour: Int get() = timeInMinutes / 60
}

data class Place(
    val name: String,
    val building: Building // LectureBuildingDto -> Building 쓰는거 아니야..?
)

data class PlaceTime(
    val timetableBlock: TimetableBlock,
    //val place: Place,
)

enum class Day {
    MON,
    TUE,
}

interface TimetableBlockInterface{
    val day: Day
    val startTime: Time
    val endTime: Time
}

data class TimetableBlock (
    override val day: Day,
    override val startTime: Time,
    override val endTime: Time,
) : TimetableBlockInterface



data class SearchTimeDto(
    override val day: Day,
    override val startTime: Time,
    override val endTime: Time,
) : TimetableBlockInterface {
    companion object {
        const val FIRST = 0
        const val MIDDAY = 720
        const val LAST = 1435
    }
}

data class Editable<T> (
    val originValue: T,
    val editedValue: T,
)

abstract class Lecture(
    open val id: String,
    open val courseTitle: String,
    open val credit: Long,
    open val placeTimes: List<PlaceTime>,
    open val instructor: String,
    open val remark: String,
)

abstract class OriginalLecture(
    override val id: String,
    override val courseTitle: String,
    override val credit: Long,
    override val instructor: String,
    override val placeTimes: List<PlaceTime>,
    override val remark: String,
    open val registrationCount: Long,
    open val wasFull: Boolean,
): Lecture(
    id = id,
    courseTitle = courseTitle,
    credit = credit,
    placeTimes = placeTimes,
    instructor = instructor,
    remark = remark
)

// 뭔가 마음에 안듬
data class BlockColor(
    val fgRaw: String? = null,
    val bgRaw: String? = null,
) {
    constructor(fgColor: Int, bgColor: Int) : this(
        "#%06X".format(0xFFFFFF and fgColor),
        "#%06X".format(0xFFFFFF and bgColor),
    )

    val fgColor: Int?
        get() = if (fgRaw != null) Color.parseColor(fgRaw) else null

    val bgColor: Int?
        get() = if (bgRaw != null) Color.parseColor(bgRaw) else null
}

abstract class TimetableLecture(
    override val id: String,
    override val courseTitle: String,
    override val credit: Long,
    override val instructor: String,
    override val placeTimes: List<PlaceTime>,
    override val remark: String,
    open val colorIndex: Long,
    open val color: BlockColor,
): Lecture(
    id = id,
    courseTitle = courseTitle,
    credit = credit,
    placeTimes = placeTimes,
    instructor = instructor,
    remark = remark
)

data class ClonedLecture(
    override val id: String,
    override val courseTitle: String,
    override val credit: Long,
    override val instructor: String,
    override val placeTimes: List<PlaceTime>,
    override val remark: String,
    override val colorIndex: Long,
    override val color: BlockColor,
    val lectureId: String,
    val classification: String,
    val department: String,
    val academicYear: String,
    val courseNumber: String,
    val lectureNumber: String,
    val quota: Long,
    val freshmanQuota: Long,
    val category: String,
): TimetableLecture(
    id = id,
    courseTitle = courseTitle,
    credit = credit,
    instructor = instructor,
    placeTimes = placeTimes,
    remark = remark,
    colorIndex = colorIndex,
    color = color
)

data class CustomLecture(
    override val id: String,
    override val courseTitle: String,
    override val credit: Long,
    override val instructor: String,
    override val placeTimes: List<PlaceTime>,
    override val remark: String,
    override val colorIndex: Long,
    override val color: BlockColor,
): TimetableLecture(
    id = id,
    courseTitle = courseTitle,
    credit = credit,
    instructor = instructor,
    placeTimes = placeTimes,
    remark = remark,
    colorIndex = colorIndex,
    color = color
)

data class AcademicPeriod(
    val semester: Long,
    val year: Long,
) : Comparable<AcademicPeriod> {
    override fun compareTo(other: AcademicPeriod): Int {
        return when {
            (year > other.year) -> -1
            (year < other.year) -> 1
            else -> {
                when{
                    (semester > other.semester) -> -1
                    (semester < other.semester) -> 1
                    else -> 0
                }
            }
        }
    }
}

data class NicknameWithTag(
    val nickname: String = "",
    val tag: String = "",
) {
    override fun toString(): String {
        return "$nickname#$tag"
    }
}

data class Notification(
    val title: String,
    val message: String,
    val createdAt: String,
    val type: Int,
    val deeplink: String?,
)

//@JsonClass(generateAdapter = true)
//data class RemoteConfigDto(
//    @Json(name = "reactNativeBundleFriends") val reactNativeBundleSrc: ReactNativeBundleSrc? = null,
//    @Json(name = "vacancyNotificationBanner") val vacancyBannerConfig: VacancyBannerConfig = VacancyBannerConfig(false),
//    @Json(name = "vacancySugangSnuUrl") val vacancyUrlConfig: VacancyUrlConfig = VacancyUrlConfig(),
//    @Json(name = "settingsBadge") val settingsBadgeConfig: SettingsBadgeConfig = SettingsBadgeConfig(),
//    @Json(name = "disableMapFeature") val disableMapFeature: Boolean? = null,
//) {
//    data class ReactNativeBundleSrc(
//        @Json(name = "src") val src: Map<String, String>,
//    )
//
//    data class SettingsBadgeConfig(
//        @Json(name = "new") val new: List<String> = emptyList(),
//    )
//
//    data class VacancyBannerConfig(
//        @Json(name = "visible") val visible: Boolean = false,
//    )
//
//    data class VacancyUrlConfig(
//        @Json(name = "url") val url: String? = null,
//    )
//}
//
//fun RemoteConfigDto.toExternalModel() = RemoteConfigNew(
//    androidReactNativeBundleSrc = reactNativeBundleSrc?.src?.get("android"),
//    vacancyNotificationBannerVisible = vacancyBannerConfig.visible,
//    vacancyUrl = vacancyUrlConfig.url,
//    settingsBadgeConfigNew = settingsBadgeConfig.new,
//    disableMapFeature = disableMapFeature,
//)

data class RemoteConfigNew(
    val androidReactNativeBundleSrc: String?,
    val vacancyNotificationBannerVisible: Boolean,
    val vacancyUrl: String?,
    val settingsBadgeConfigNew: List<String>,
    val disableMapFeature: Boolean?
)


fun main(){
    val a = TimetableBlock(Day.TUE, Time(250), Time(350))

//    val config = MutableStateFlow(RemoteConfigDto().toExternalModel())
//    val friendsBundleSrc: Flow<String>
//        get() = config.map { it.androidReactNativeBundleSrc }.filterNotNull()
//
//    val config2 = MutableStateFlow(RemoteConfigDto())
//    val friendsBundleSrc2: Flow<String>
//        get() = config2.map { it.reactNativeBundleSrc?.src?.get("android") }.filterNotNull()
}
