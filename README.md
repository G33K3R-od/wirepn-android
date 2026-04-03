# WirePN (Android)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/about/versions/oreo)
[![CI](https://github.com/G33K3R-od/wirepn-android/actions/workflows/ci.yml/badge.svg)](https://github.com/G33K3R-od/wirepn-android/actions/workflows/ci.yml)
[![Release](https://github.com/G33K3R-od/wirepn-android/actions/workflows/release.yml/badge.svg)](https://github.com/G33K3R-od/wirepn-android/actions/workflows/release.yml)

A **WireGuard** client in the **WirePN** family: import profiles, manage tunnels, connect and disconnect using the standard Android VPN API.

**Latest release:** [v0.1.0](https://github.com/G33K3R-od/wirepn-android/releases/tag/v0.1.0) · [CHANGELOG.md](CHANGELOG.md)

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

### Release signing (local installs & stores)

Release builds use **`keystore.properties`** at the **repository root** (not committed). Copy [`keystore.properties.example`](keystore.properties.example) to `keystore.properties` and set:

- `storeFile` — path to your `.jks` / `.keystore` (relative to repo root or absolute)
- `storePassword`, `keyAlias`, `keyPassword`

Alternatively, set environment variables (same values as in CI): `SIGNING_STORE_FILE`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`.

Then:

```bash
./gradlew assembleRelease
# Play-style bundle:
./gradlew bundleRelease
```

**Without** `keystore.properties` / env, `assembleRelease` **fails locally** (no fallback), so you do not accidentally ship an unsigned release. **CI** without secrets still builds a **debug-signed** release APK for testing (see below).

### CI release APK

The [Release APK](.github/workflows/release.yml) workflow:

1. **Optional — real signing:** add these **repository secrets** (Settings → Secrets and variables → Actions). If `RELEASE_KEYSTORE_BASE64` is set, the workflow decodes it and signs the release APK with your upload key (suitable for sideloading and aligned with Play/AppGallery upload keys if you use the same keystore).

   | Secret | Meaning |
   |--------|---------|
   | `RELEASE_KEYSTORE_BASE64` | Base64-encoded `.jks` / `.keystore` file |
   | `RELEASE_STORE_PASSWORD` | Keystore password |
   | `RELEASE_KEY_ALIAS` | Key alias |
   | `RELEASE_KEY_PASSWORD` | Private key password |

   Encode the file (example): `base64 -w0 my-release.jks` (Linux/macOS) or on Windows use WSL / OpenSSL / a small script — the output is one line for the secret value.

2. **If those secrets are missing:** the workflow still runs with **`CI=true`**, and the release build is signed with the **ephemeral CI debug keystore** (installable for tests; not for store uploads).

- **Tag push** (`v*`, e.g. `v0.1.0`): uploads the APK to the workflow run **and** attaches it to a **GitHub Release** for that tag.
- **Release notes:** add `RELEASE_NOTE_<tag>.md` at the repo root (e.g. `RELEASE_NOTE_v0.1.0.md`). If present, it becomes the release description; otherwise GitHub auto-generates notes from commits.
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
