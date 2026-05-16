# Proguard for firebase crashlytics (https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android)
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# SNUTTStorage 가 Moshi reflection (KotlinJsonAdapterFactory) 으로 직렬화하는 storage 전용 모델.
# 모든 LocalEntity / NetworkLog / Optional wrapper 가 이 패키지에 위치한다 (enum 포함).
-keep class com.wafflestudio.snutt2.storage.model.** { *; }

# Retrofit + Moshi reflection 으로 직/역직렬화되는 네트워크 DTO.
-keep class com.wafflestudio.snutt2.network.** { *; }

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
