# Proguard rules for habit tracker
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
