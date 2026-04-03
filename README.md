# WirePN (Android)

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/about/versions/oreo)
[![CI](https://github.com/G33K3R-od/wirepn-android/actions/workflows/ci.yml/badge.svg)](https://github.com/G33K3R-od/wirepn-android/actions/workflows/ci.yml)

Мобильный клиент **WireGuard** в линейке **WirePN**: импорт профилей, список туннелей, подключение и отключение через стандартный Android VPN API.

| | |
|---|---|
| **Исходный код** | [github.com/G33K3R-od/wirepn-android](https://github.com/G33K3R-od/wirepn-android) |
| **Страница проекта (GitHub)** | [github.com/G33K3R-od/WireGuard-app](https://github.com/G33K3R-od/WireGuard-app) |
| **Лицензия** | [MIT](LICENSE) |

## Возможности

- Импорт конфигурации из `.conf` или из текста буфера обмена
- Несколько профилей и выбор активного
- Состояние подключения и краткие сообщения об ошибках на экране
- Нижняя навигация: Connect / Profiles / Settings; в debug-сборке дополнительно экран Logs
- Тема: системная / светлая / тёмная; локализация **en** по умолчанию, **ru** в `values-ru/`
- Хранение конфигураций и ключей в **EncryptedSharedPreferences** (AES-GCM)

Туннель построен на официальной библиотеке [`com.wireguard.android:tunnel`](https://github.com/WireGuard/wireguard-android) (**wireguard-go**, `GoBackend`).

## Требования

| Компонент | Версия |
|-----------|--------|
| Android Studio | Koala (2024.1.1) или новее |
| Android Gradle Plugin | 8.7.x (`gradle/libs.versions.toml`) |
| JDK | 17 |
| minSdk / targetSdk | 26 / 35 |
| Android SDK Platform | 35 |

`local.properties` (создаётся Android Studio, в репозиторий не коммитится):

```properties
sdk.dir=C\:\\Users\\You\\AppData\\Local\\Android\\Sdk
```

На Linux/macOS: `sdk.dir=/path/to/Android/sdk`.

## Сборка

```bash
# Windows
gradlew.bat lint assembleDebug

# Linux / macOS
./gradlew lint assembleDebug
```

- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk` (у debug — суффикс `applicationId`: `.debug`).
- **Release:** настройте `signingConfigs` в `app/build.gradle.kts` и подпись через свой keystore; ключи не храните в git.

## Архитектура (`:app`)

| Пакет | Назначение |
|-------|------------|
| `com.wirepn.android.data` | Модель профиля, EncryptedSharedPreferences, репозиторий |
| `com.wirepn.android.vpn` | Обёртка над WireGuard (`GoBackend`, `Tunnel`) |
| `com.wirepn.android.ui` | Jetpack Compose, Material 3, экраны, тема |

**Стек:** Compose BOM, Material 3, Navigation Compose, Kotlin Serialization, `androidx.security:security-crypto`. DI — `Application` и фабрика `ViewModel`.

## Безопасность

Не логируйте в релизе полный конфиг и приватные ключи. Текущий код не пишет содержимое конфигурации в лог.

Сообщения об уязвимостях: [SECURITY.md](SECURITY.md).

## Участие в разработке

См. [CONTRIBUTING.md](CONTRIBUTING.md).

## Лицензия

Проект — [MIT](LICENSE). Библиотека туннеля WireGuard распространяется под Apache-2.0 (артефакт `com.wireguard.android:tunnel`).
