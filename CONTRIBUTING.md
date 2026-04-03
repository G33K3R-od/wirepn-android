# Contributing to WirePN (Android)

Thanks for your interest. This document describes the workflow and expectations for contributions.

## Environment

- **JDK 17**
- **Android Studio** with an up-to-date Android SDK (Platform **35**)
- Clone and build from the repository root; SDK path goes in `local.properties` (local file, not committed)

Before opening a PR, run the same checks as CI:

```bash
./gradlew lint assembleDebug
```

On Windows: `gradlew.bat lint assembleDebug`.

## How to propose changes

1. Branch off `main` with a clear name, e.g. `fix/connection-state` or `feat/profile-import`.
2. One PR per logical change; large refactors are better discussed first (issue or draft PR).
3. In the PR description, explain **what** changed and **why**; for bug fixes, include how to reproduce and expected behavior after the fix.
4. Ensure `lint` and `assembleDebug` pass locally.

## Code style

- **Kotlin**, **Jetpack Compose**, **Material 3** — follow patterns in `app/src/main/java/com/wirepn/android/`.
- Naming and package layout should match the existing code; avoid speculative abstractions.
- User-visible strings: prefer resources; for new copy, consider **en** and **ru** (`values-ru/`) where appropriate.

## Security and secrets

- Do not commit keystores, passwords, full WireGuard configs with keys, or sensitive paths in `local.properties`.
- Do not commit `keystore.properties` (it is gitignored). Release signing is documented in [`README.md`](README.md) (local file + optional GitHub Actions secrets `RELEASE_*`).
- Do not add logging of private keys or full configuration text.

## License

By contributing code, you agree it will be distributed under the project [LICENSE](LICENSE) (MIT), unless explicitly stated otherwise.

## Questions

If behavior or scope is unclear, open an issue with a short proposal so we can align before you invest a lot of time.
