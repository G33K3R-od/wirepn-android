# WirePN для Android

Клиент **WireGuard** в линейке **WirePN**: импорт профилей, список туннелей, подключение и отключение через стандартный Android VPN API.

**Репозиторий:** [github.com/G33K3R-od/WireGuard-app](https://github.com/G33K3R-od/WireGuard-app)

## Возможности

- Импорт конфигурации из `.conf` или из текста буфера обмена
- Несколько профилей и выбор активного
- Состояние подключения и краткие сообщения об ошибках на экране
- Нижняя навигация: Connect / Profiles / Settings; в debug-сборке дополнительно Logs
- Тема: системная / светлая / тёмная; локализация **en** по умолчанию, **ru** в `values-ru/`
- Хранение конфигураций и ключей в **EncryptedSharedPreferences** (AES-GCM)

Туннель реализован на официальной библиотеке [`com.wireguard.android:tunnel`](https://github.com/WireGuard/wireguard-android) (**wireguard-go**, `GoBackend`), без самописного криптопротокола.

## Требования

| Компонент | Версия |
|-----------|--------|
| Android Studio | Koala (2024.1.1) или новее |
| Android Gradle Plugin | 8.7.x (см. `gradle/libs.versions.toml`) |
| JDK | 17 |
| minSdk / targetSdk | 26 / 35 |
| Android SDK Platform | 35 |

Путь к SDK задаётся в `local.properties` (файл обычно создаёт Android Studio):

```properties
sdk.dir=C\:\\Users\\You\\AppData\\Local\\Android\\Sdk
```

На Linux/macOS используйте обычный путь, например `/path/to/Android/sdk`.

## Сборка

Из корня проекта:

```bash
# Windows (PowerShell / CMD)
gradlew.bat assembleDebug

# Linux / macOS
./gradlew assembleDebug
```

**Debug APK:** `app/build/outputs/apk/debug/app-debug.apk` (у debug-сборки суффикс `applicationId`: `.debug`).

**Release** (нужен свой keystore):

```bash
gradlew.bat assembleRelease
```

Настройте `signingConfigs` в `app/build.gradle.kts` и не коммитьте ключи в репозиторий.

**Проверка lint:**

```bash
gradlew.bat lint
```

## Архитектура (модуль `app`)

| Пакет | Назначение |
|-------|------------|
| `com.wirepn.android.data` | Модель профиля, EncryptedSharedPreferences, репозиторий |
| `com.wirepn.android.vpn` | Обёртка над WireGuard (`GoBackend`, `Tunnel`) |
| `com.wirepn.android.ui` | Jetpack Compose, Material 3, экраны, тема |

**Стек:** Compose BOM, Material 3, Navigation Compose, Kotlin Serialization, `androidx.security:security-crypto`. DI — через `Application` и фабрику `ViewModel`.

## Безопасность

В релизной сборке не логируйте полный конфиг и приватные ключи. Текущая реализация не пишет содержимое конфигурации в лог.

## Лицензия

Проект распространяется под [MIT](LICENSE). Библиотека туннеля WireGuard — под Apache-2.0 (артефакт `com.wireguard.android:tunnel`).
