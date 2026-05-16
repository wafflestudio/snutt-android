# Proguard for firebase crashlytics (https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android)
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# Moshi codegen 으로 ${ClassName}JsonAdapter 가 생성되어 reflection-free 로 직/역직렬화한다.
# Moshi 는 ${ClassName}JsonAdapter 를 reflection 으로 lookup 하므로 DTO 와 generated adapter 의
# 클래스명이 paired 로 보존되어야 한다. generated adapter 가 DTO 생성자/property 를 직접 호출하므로
# R8 이 멤버 그래프를 추적할 수 있어 멤버 전체 keep 은 불필요하다.
-keep @com.squareup.moshi.JsonClass class * {
    <init>(...);
}
-keep class **JsonAdapter { <init>(...); }

# SharedPreference 직렬화 ABI: Moshi 가 enum 을 enum.name 으로 직렬화하므로 value 이름이 변하면
# 기존 사용자 기기에 저장된 JSON 과 매칭이 깨진다. @JsonClass 가 붙은 enum 은 Moshi 번들 룰이
# 보호하지만 (META-INF/proguard/moshi.pro), 어노테이션 없는 enum 도 명시 보존한다.
-keepclassmembers enum com.wafflestudio.snutt2.network.dto.** {
    **[] values();
    <fields>;
}
-keepclassmembers enum com.wafflestudio.snutt2.storage.model.** {
    **[] values();
    <fields>;
}

# Retrofit 이 런타임 동적 proxy 로 구현하는 API 인터페이스.
-keep interface com.wafflestudio.snutt2.network.api.** { *; }

# https://github.com/square/retrofit/issues/3751#issuecomment-1192043644
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# For Kakao SDK
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# https://github.com/square/okhttp/pull/6792
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**
