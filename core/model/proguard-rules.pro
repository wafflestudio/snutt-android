# Proguard for firebase crashlytics (https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android)
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

# class stored to sharedPreference after serialization
-keep public enum com.wafflestudio.snutt2.core.model.** { *; }
-keep class com.wafflestudio.snutt2.core.model.data.** { *; }

# For Kakao SDK
-keep class com.kakao.sdk.**.model.* { <fields>; }

# https://github.com/square/okhttp/pull/6792
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**
