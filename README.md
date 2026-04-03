# WirePN (Android)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/about/versions/oreo)
[![CI](https://github.com/G33K3R-od/wirepn-android/actions/workflows/ci.yml/badge.svg)](https://github.com/G33K3R-od/wirepn-android/actions/workflows/ci.yml)
[![Release](https://github.com/G33K3R-od/wirepn-android/actions/workflows/release.yml/badge.svg)](https://github.com/G33K3R-od/wirepn-android/actions/workflows/release.yml)

A **WireGuard** client in the **WirePN** family: import profiles, manage tunnels, connect and disconnect using the standard Android VPN API.

| | |
|---|---|
| **Source code** | [github.com/G33K3R-od/wirepn-android](https://github.com/G33K3R-od/wirepn-android) |
| **Project page (GitHub)** | [github.com/G33K3R-od/WireGuard-app](https://github.com/G33K3R-od/WireGuard-app) |
| **License** | [MIT](LICENSE) |

## Features

- Import configuration from a `.conf` file or clipboard text
- Multiple profiles and an active profile selector
- Connection status and short error messages on screen
- Bottom navigation: Connect / Profiles / Settings; debug builds also include a Logs screen
- Theme: system / light / dark; default **en** strings, **ru** in `values-ru/`
- Encrypted storage for configs and keys via **EncryptedSharedPreferences** (AES-GCM)

The tunnel uses the official [`com.wireguard.android:tunnel`](https://github.com/WireGuard/wireguard-android) library (**wireguard-go**, `GoBackend`).

## Requirements

| Component | Version |
|-----------|---------|
| Android Studio | Koala (2024.1.1) or newer |
| Android Gradle Plugin | 8.7.x (see `gradle/libs.versions.toml`) |
| JDK | 17 |
| minSdk / targetSdk | 26 / 35 |
| Android SDK Platform | 35 |

`local.properties` (created by Android Studio; not committed):

```properties
sdk.dir=C\:\\Users\\You\\AppData\\Local\\Android\\Sdk
```

On Linux/macOS: `sdk.dir=/path/to/Android/sdk`.

## Build

```bash
# Windows
gradlew.bat lint assembleDebug

# Linux / macOS
./gradlew lint assembleDebug
```

- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk` (debug uses `applicationId` suffix `.debug`).
- **Release (local):** configure `signingConfigs` in `app/build.gradle.kts` and sign with your own keystore; never commit signing keys.

### CI release APK

The [Release APK](.github/workflows/release.yml) workflow builds a signed **release** APK on GitHub Actions (signed with the CI debug keystore so the artifact is installable without repository secrets).

- **Tag push** (`v*`, e.g. `v0.1.0`): uploads the APK to the workflow run **and** attaches it to a **GitHub Release** for that tag.
- **Release notes:** add a Markdown file at the repo root named `RELEASE_NOTE_<tag>.md` (e.g. `RELEASE_NOTE_v0.1.0.md` for tag `v0.1.0`). If it exists, its contents become the release description; otherwise GitHub auto-generates notes from commits.
- **Manual run** (*Actions → Release APK → Run workflow*): uploads the APK as a workflow **artifact** only (no GitHub Release).

## Architecture (`:app`)

| Package | Role |
|---------|------|
| `com.wirepn.android.data` | Profile model, EncryptedSharedPreferences, repository |
| `com.wirepn.android.vpn` | WireGuard wrapper (`GoBackend`, `Tunnel`) |
| `com.wirepn.android.ui` | Jetpack Compose, Material 3, screens, theme |

**Stack:** Compose BOM, Material 3, Navigation Compose, Kotlin Serialization, `androidx.security:security-crypto`. DI via `Application` and `ViewModel` factory.

## Security

Do not log full configs or private keys in release builds. The current code does not log configuration contents.

Vulnerability reports: [SECURITY.md](SECURITY.md).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

This project is [MIT](LICENSE). The WireGuard tunnel library is licensed under Apache-2.0 (artifact `com.wireguard.android:tunnel`).
