# Changelog

All notable changes to this project are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-04-03

### Added

- Initial release: WireGuard VPN client (WirePN family) for Android.
- Profile import from `.conf` file or clipboard; multiple profiles and active profile selection.
- Connect/disconnect via Android VPN API; connection status and error hints on Connect screen.
- Encrypted storage for WireGuard configs and keys (EncryptedSharedPreferences, AES-GCM).
- Jetpack Compose UI with Material 3: Connect, Profiles, Settings; Logs screen in debug builds.
- Theme: system / light / dark; English and Russian strings.
- CI: lint + debug build on push/PR; release APK workflow on `v*` tags with optional `RELEASE_NOTE_<tag>.md`.

[0.1.0]: https://github.com/G33K3R-od/wirepn-android/releases/tag/v0.1.0
