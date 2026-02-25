# Swiss Knife

> Random utility tools for everyday decisions — coin flip, dice roll, random number, secret santa, and more.

[![CI](https://github.com/alvarotorresc/swiss-knife-android/actions/workflows/ci.yml/badge.svg)](https://github.com/alvarotorresc/swiss-knife-android/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)

<!-- ![Screenshot](screenshot.png) -->

## What it is

A minimalist Android app with a collection of random utility tools. Light and dark theme (follows system), Inter font, clean modern design, zero bloat. All tools work offline with no permissions needed.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Navigation**: Navigation Compose
- **Font**: Inter (bundled)
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
| `./gradlew test` | Run unit tests |
| `./gradlew ktlintCheck` | Run linter |
| `./gradlew ktlintFormat` | Auto-fix lint issues |

## Tools

- **Coin Flip** — Heads or tails with 3D flip animation and stats counter
- **Dice Roll** — D4/D6/D8/D10/D12/D20 with 1-4 dice and canvas-drawn faces
- **Random Number** — Min/max range generator with validation
- **Secret Santa** — Derangement algorithm, reveal one by one, share via Intent
- **Random from List** — Add items and pick a random winner
- **Fortune Wheel** — Visual spinning wheel with animated rotation
- **Finger Picker** — Multi-touch finger selection with countdown
- **Password Generator** — Configurable secure password generation (SecureRandom)

## License

MIT — see [LICENSE](./LICENSE)
