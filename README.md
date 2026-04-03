# WirePN Android

Мобильный клиент **WireGuard** в линейке **WirePN** (рядом с десктопным [WirePN Windows](https://github.com/your-org/wirepn-windows) — замените ссылку на фактический репозиторий Electron-клиента).

## Назначение

Импорт профилей WireGuard (`.conf` или текст), список профилей с выбором активного, подключение и отключение туннеля через стандартные Android VPN API. Состояние подключения и краткие сообщения об ошибках отображаются на экране. Конфигурации и ключи хранятся в **EncryptedSharedPreferences** (AES-GCM).

## Требования

- **Android Studio** Koala (2024.1.1) или новее с Android Gradle Plugin 8.7+
- **JDK 17**
- **minSdk 26** (Android 8.0), **targetSdk 35**
- Android SDK Platform 35 (ставится через SDK Manager)

Укажите путь к SDK в `local.properties` (файл создаётся Android Studio автоматически):

```properties
sdk.dir=/path/to/Android/sdk
```

## Структура модулей (текущая итерация)

| Модуль | Роль |
|--------|------|
| `:app` | Единственный модуль MVP: UI (Jetpack Compose, Material 3), `ViewModel`, репозиторий профилей, контроллер туннеля |

Пакеты внутри `app`:

- `com.wirepn.android.data` — модель профиля, `EncryptedSharedPreferences`, репозиторий
- `com.wirepn.android.vpn` — обёртка над официальной библиотекой `com.wireguard.android:tunnel` (`GoBackend`, `Tunnel`)
- `com.wirepn.android.ui` — экраны и тема

**Зависимости:** Compose BOM, Material 3, **Navigation Compose** (нижняя навигация: Connect / Profiles / Settings; в debug — Logs), Material Icons Extended, Kotlin Serialization, `androidx.security:security-crypto`. Тема и палитра выровнены с **WirePN Windows** (зелёный акцент, нейтральные фоны; IBM Plex Sans + IBM Plex Mono в `res/font`). Строки: **en** по умолчанию, **ru** в `values-ru/`. Настройки темы: системная / светлая / тёмная. DI — `Application` + фабрика `ViewModel`.

Туннель реализован через **wireguard-go** в составе артефакта WireGuard (`GoBackend`), без самописного криптопротокола.

## Сборка

**Debug:**

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk` (с суффиксом `applicationId` `.debug`).

**Release** (подпись через свой keystore):

```bash
./gradlew assembleRelease
```

Настройте `signingConfigs` в `app/build.gradle.kts` и храните ключи вне репозитория.

**Lint:**

```bash
./gradlew lint
```

## Безопасность и логи

В релизной сборке не добавляйте логирование полного конфига и приватных ключей. Текущий код не пишет содержимое конфигурации в лог.

## Лицензия

MIT — см. [LICENSE](LICENSE). Библиотека туннеля WireGuard распространяется под Apache-2.0 (см. артефакт `com.wireguard.android:tunnel`).
