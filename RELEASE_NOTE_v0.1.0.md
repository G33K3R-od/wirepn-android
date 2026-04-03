# WirePN for Android — v0.1.0

First public release of the WirePN Android client: WireGuard tunnels with a Material 3 UI, encrypted profile storage, and English/Russian UI strings.

## Highlights

- **WireGuard** connectivity via the official [`wireguard-android` tunnel](https://github.com/WireGuard/wireguard-android) library (wireguard-go / `GoBackend`).
- **Profiles:** import from `.conf` or clipboard, multiple profiles, pick the active one.
- **VPN:** connect/disconnect through the standard Android VPN API; status and short error messages on the main screen.
- **Security:** configs and keys stored with **EncryptedSharedPreferences** (AES-GCM). Release builds do not log full configuration contents.

## UI / UX

- Bottom navigation: **Connect**, **Profiles**, **Settings**; debug builds also include **Logs**.
- Theme: system / light / dark.
- Localization: **English** (default) and **Russian** (`values-ru/`).

## Requirements

- Android **8.0+** (API 26+), target SDK **35**.

## Build & distribution

- CI builds a **release APK** on tag pushes (`v*`). GitHub Actions signs that artifact with the **CI debug keystore** so it is installable without repository secrets — suitable for testing and sideloading, not for Play/AppGallery store signing with your own key.

## Known limitations

- Store distribution (Google Play, AppGallery) requires your own signing and store-specific steps; see project documentation.

---

**Full source:** [wirepn-android](https://github.com/G33K3R-od/wirepn-android) · **License:** [MIT](LICENSE)
