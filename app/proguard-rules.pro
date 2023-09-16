# Proguard for firebase crashlytics (https://firebase.google.com/docs/crashlytics/get-deobfuscated-reports?platform=android)
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
-keep public enum com.wafflestudio.snutt2.**{
    *;
}
-keep class com.wafflestudio.snutt2.* { *; }
-keepattributes InnerClasses
