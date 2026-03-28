# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard-rules.pro file.

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Retrofit interfaces
-keep,allowobfuscation interface com.bear.asset.data.remote.ApiService

# Keep Gson serialized classes
-keepattributes Signature
-keepattributes *Annotation*
