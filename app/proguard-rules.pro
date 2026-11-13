# SpendWise ProGuard / R8 Optimization Rules

# Preserve stack traces and line numbers for crash analysis
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# --- Android Components ---
-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# --- SpendWise Application & App Widget ---
-keep class com.example.spendwise.SpendWiseApp { *; }
-keep class com.example.spendwise.QuickAddWidgetProvider { *; }

# --- Android Room (Keep all Entities, DAOs, Database & Models intact) ---
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class com.example.spendwise.data.** { *; }
-dontwarn androidx.room.paging.**

# --- WorkManager (Keep Worker constructors instantiated by reflection) ---
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep public class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.example.spendwise.workers.** { *; }

# --- MPAndroidChart (Uses reflection for values and animators) ---
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# --- Lottie Animations ---
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# --- UI, ViewModels, and Fragments ---
-keep class * extends androidx.fragment.app.Fragment { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class com.example.spendwise.ui.** { *; }

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# --- Annotations & Okio ---
-dontwarn javax.annotation.**
-dontwarn javax.annotation.meta.**
-dontwarn okio.**


