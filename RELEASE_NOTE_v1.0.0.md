# WirePN for Android — v1.0.0

Stable release with **per-app VPN routing**, clearer settings copy, and faster loading for large app lists.

## Highlights

- **Per-app routing:** choose **bypass VPN** (selected apps use normal internet) or **VPN only** (whitelist). Settings are applied to the WireGuard tunnel (`ExcludedApplications` / `IncludedApplications`).
- **App list:** full installed package list where the OS allows; metadata loads off the UI thread; icons load lazily while scrolling.
- **Auto re-apply:** when routing settings change while the VPN is connected, the active profile is reapplied automatically (debounced).
- **Settings:** scrollable layout; clearer text for Android system VPN options and app routing (English and Russian).

## Requirements

- Android **8.0+** (API 26+), target SDK **35**.

## Build & distribution

- **Local signed release:** configure `keystore.properties` (see README), then `./gradlew assembleRelease`. Output: `app/build/outputs/apk/release/`.
- **Without a keystore**, Gradle still builds an **unsigned** APK — sign it before sideload, or use **CI** (workflow signs with the CI debug keystore when upload secrets are absent).
- **Quick install (debug):** `./gradlew assembleDebug` → `app-debug.apk` (`com.wirepn.android.debug`).

## Previous release

See [v0.1.0](https://github.com/G33K3R-od/wirepn-android/releases/tag/v0.1.0).

---

**Source:** [wirepn-android](https://github.com/G33K3R-od/wirepn-android) · **License:** [MIT](LICENSE)
