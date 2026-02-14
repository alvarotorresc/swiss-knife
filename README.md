# Swiss Knife

> Random utility tools for everyday decisions — coin flip, dice roll, random number, secret santa.

[![CI](https://github.com/alvarotorresc/swiss-knife-android/actions/workflows/ci.yml/badge.svg)](https://github.com/alvarotorresc/swiss-knife-android/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)

<!-- ![Screenshot](screenshot.png) -->

## What it is

A minimalist Android app with a collection of random utility tools. Dark theme, clean design, zero bloat. Flip a coin, roll dice, generate random numbers, or organize a secret santa — all offline, no permissions needed.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Navigation**: Navigation Compose
- **Min SDK**: API 26 (Android 8.0)
- **Distribution**: Google Play Store + APK

## Local Development

### Prerequisites

- Android Studio (latest stable)
- JDK 21

### Setup

```bash
git clone https://github.com/alvarotorresc/swiss-knife-android.git
cd swiss-knife-android
```

Open in Android Studio, sync Gradle, and run on emulator or device.

### Commands

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew bundleRelease` | Build release AAB |
| `./gradlew testDebugUnitTest` | Run unit tests |
| `./gradlew ktlintCheck` | Run linter |
| `./gradlew ktlintFormat` | Auto-fix lint issues |

## Features

- **Coin Flip** — Heads or tails with 3D flip animation and stats counter
- **Dice Roll** — 1-4 configurable dice with canvas-drawn faces
- **Random Number** — Min/max range generator with validation
- **Secret Santa** — Derangement algorithm, reveal one by one, share via Android Intent

## License

MIT — see [LICENSE](./LICENSE)
