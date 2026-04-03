# WireGuard tunnel (JNI / wireguard-go)
-keep class com.wireguard.** { *; }
-dontwarn com.wireguard.**

# Tink / EncryptedSharedPreferences (optional annotations)
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn javax.annotation.concurrent.**
