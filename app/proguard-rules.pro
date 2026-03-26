-dontwarn sun.security.internal.spec.**
-dontwarn sun.security.provider.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.lang.model.element.Modifier

-keepattributes Exceptions,LineNumberTable,Signature,SourceFile

-keepclasseswithmembernames,allowoptimization,allowobfuscation class * {
    native <methods>;
}

-keepclassmembers,allowoptimization,allowobfuscation enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Activity and Fragment names
-keep class com.drdisagree.pixellauncherenhanced.ui.activities.**
-keep class com.drdisagree.pixellauncherenhanced.ui.fragments.**

# Xposed
-keep class de.robv.android.xposed.**
-keep class com.drdisagree.pixellauncherenhanced.xposed.InitHook {
    <init>();
}
-keepnames class com.drdisagree.pixellauncherenhanced.xposed.**
-keepnames class com.drdisagree.pixellauncherenhanced.xposed.utils.XPrefs
-keep class com.drdisagree.pixellauncherenhanced.xposed.** {
    <init>(android.content.Context);
}

# Keep Parcelable Creators
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# AIDL Classes
-keep interface **.I* { *; }
-keep class **.I*$Stub { *; }
-keep class **.I*$Stub$Proxy { *; }

# Obfuscation
-repackageclasses
-allowaccessmodification

# Strip debug log
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
}

# Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	public static void check*(...);
	public static void throw*(...);
}
-assumenosideeffects class java.util.Objects {
    public static ** requireNonNull(...);
}
