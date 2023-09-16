# Proguard for firebase crashlytics (https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android)
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
-keep public enum com.wafflestudio.snutt2.**{
    *;
}
-keep class com.wafflestudio.snutt2.** { *; }
-keepattributes InnerClasses

 # Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
 -keep,allowobfuscation,allowshrinking interface retrofit2.Call
 -keep,allowobfuscation,allowshrinking class retrofit2.Response

 # With R8 full mode generic signatures are stripped for classes that are not
 # kept. Suspend functions are wrapped in continuations where the type argument
 # is used.
 -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation