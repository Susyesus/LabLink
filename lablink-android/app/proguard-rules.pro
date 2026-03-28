# Add project specific ProGuard rules here.
# Keep Retrofit + Gson models
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.lablink.android.data.model.** { *; }
-keep class retrofit2.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
