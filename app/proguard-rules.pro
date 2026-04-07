# Project specific ProGuard rules

# Keep Room related classes
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao

# Hilt rules
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class com.google.dagger.** { *; }
-keep class dagger.hilt.** { *; }
-keep class dagger.internal.DoubleCheck { *; }

# Compose rules (usually handled by the compiler but good to keep common ones)
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext$HandlerPost {
    *** run();
}

# Preserve line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# If you use Gson or other serialization libraries, add their rules here
# -keep class veto.call.blocker.data.entity.** { *; }
