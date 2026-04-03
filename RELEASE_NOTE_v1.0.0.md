# WirePN for Android — v1.0.0

Stable release with **per-app VPN routing**, clearer settings copy, and faster loading for large app lists.

Full list of changes: [CHANGELOG.md](CHANGELOG.md) (section **[1.0.0]**).

## Highlights

- **Per-app routing:** **Bypass VPN** (selected apps use normal internet) or **VPN only** (whitelist). Applied via WireGuard `ExcludedApplications` / `IncludedApplications` in the tunnel config.
- **App list:** full installed package list where the OS allows (`QUERY_ALL_PACKAGES` when granted); metadata loads off the UI thread; icons load lazily; spinner while the list is loading.
- **Auto re-apply:** when routing settings change while the VPN is connected, the active profile is reapplied automatically (debounced).
- **Settings:** scrollable screen; clearer copy for Android system VPN options and app routing (EN/RU).
- **UI:** segmented mode control and search on the routing screen; removed the thin accent line under the status bar (edge-to-edge).

## Requirements

- Android **8.0+** (API **26+**), target SDK **35**.

## Build & distribution

- **Signed release APK:** configure `keystore.properties` (see [README](README.md)), then `./gradlew assembleRelease`. Artifact: `app/build/outputs/apk/release/app-release.apk` (or `app-release-unsigned.apk` if signing is not configured).
- **Without a keystore**, Gradle can still produce an **unsigned** APK — sign it before sideload, or use **CI** (the workflow signs with the CI debug keystore when upload secrets are absent).
- **Debug sideload:** `./gradlew assembleDebug` → `app-debug.apk` (`com.wirepn.android.debug`).
- **GitHub Release:** push tag `v1.0.0` — the [Release APK](.github/workflows/release.yml) workflow attaches the APK and uses this file as the release description when present.

## Previous release

[v0.1.0](https://github.com/G33K3R-od/wirepn-android/releases/tag/v0.1.0) — first public build.

---

**Source:** [wirepn-android](https://github.com/G33K3R-od/wirepn-android) · **License:** [MIT](LICENSE)

### Кратко (RU)

Маршрутизация по приложениям (обход VPN / только VPN), полный список приложений с быстрой загрузкой, автопереподключение при смене списка при активном VPN, прокрутка и понятные тексты в настройках, правки интерфейса экрана выбора приложений. Подробности — в [CHANGELOG](CHANGELOG.md).
