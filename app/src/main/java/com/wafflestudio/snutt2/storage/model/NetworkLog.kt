package com.wafflestudio.snutt2.storage.model

/**
 * 디버그 빌드에서 OkHttp 인터셉터가 캡처해 SNUTTStorage 에 저장하는 네트워크 호출 기록.
 * 도메인 모델이 아니고 단순 디버깅 데이터 클래스이므로 별도 LocalEntity 와 도메인 모델을 분리하지 않는다.
 */
data class NetworkLog(
    val requestMethod: String,
    val requestUrl: String,
    val requestHeader: String,
    val requestBody: String,
    val responseCode: String,
    val responseBody: String,
)
