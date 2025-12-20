# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider

# --- Gson needs generics + annotations ---
# Keep generic type info (for List<T> or Map<String,T>)

-keep class org.readium.r2.shared.** { *; }
-dontwarn org.readium.r2.shared.**
-keep class org.readium.r2.streamer.** {*;}
-dontwarn org.readium.r2.streamer.**
-keep class org.readium.r2.navigator.** {*;}
-dontwarn org.readium.r2.navigator.**
-keep class org.readium.r2.opds.** {*;}
-dontwarn org.readium.r2.opds.**
-keep class org.readium.r2.lcp.** {*;}
-dontwarn org.readium.r2.lcp.**
-keep class org.readium.lcp.** {*;}
-dontwarn org.readium.lcp.**





