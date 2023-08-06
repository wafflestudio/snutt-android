import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigDto(
    @Json(name = "reactNativeBundleFriends") val friends: ReactNativeBundleSrc? = null,
)

data class ReactNativeBundleSrc(
    @Json(name = "src") val src: Map<String, String>
)
