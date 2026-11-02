# Leaf Board 🌿 — A Clean, Privacy-First Android Keyboard

> [!IMPORTANT]
> 🛡️ **Custom build.** Leaf Board is a cleaned, rebranded build based on the open-source Orbit Board / FUTO Keyboard code (Apache 2.0 — see LICENSE.md and NOTICE). The built-in Whisper voice engine, update checker, announcements, crash reporting, and a hidden screenshot "AI assistant" found in the original source have been removed at the code level. Voice typing is still available — via your phone's own system voice keyboard (e.g. Google Voice Typing), on an explicit tap; this app records no audio itself — see `CUSTOM_BUILD_NOTES.md` for exactly what was cut and what remains. Build the APK from this source with the GitHub Actions workflow in this repo; do not install prebuilt APKs from other repositories.


![Release](https://img.shields.io/badge/Release-v1.0.8%20Stable-E5252A?style=for-the-badge&logo=android&logoColor=white)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue?style=for-the-badge)](LICENSE.md)
[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024--35)-black?style=for-the-badge&logo=google)](https://developer.android.com)
![Privacy](https://img.shields.io/badge/Privacy-No%20Tracking-00A86B?style=for-the-badge&logo=shield)
![Architecture](https://img.shields.io/badge/Architecture-ARM64%20%7C%20Universal-darkred?style=for-the-badge&logo=arm)

**Leaf Board** is a modern, high-performance, privacy-first Android virtual keyboard crafted with a **clean, light design language**. Designed specifically for enthusiasts who value minimalism, tactile beauty, and absolute privacy, Leaf Board combines the signature **Leaf palette**, clean Inter typography, rounded Gboard-style keys, and an advanced **Native Bengali Typing Suite** (Avro Phonetic, Probhat, and Jatiyo).

No tracking. No logging. No cloud — except one honest exception: Translate, only when you tap it. Just pure, elevated typing.

---

## ⚡ Quick Download

There is no prebuilt APK for this custom build — deliberately. Build it from this exact source with the GitHub Actions workflow in this repo (Actions → "Build Leaf Board (Custom)" → Run workflow) and download the APK from the run's artifacts, so the binary you install is verifiably built from this code and nothing else.

---

## 🌟 Why Leaf Board?

### 🎨 1. Nothing OS & Bauhaus Design Language
- **Leaf palette**: Handcrafted matte tones designed for high legibility and OLED battery conservation:
  - 🔴 **Matte Carmine Red** (`#E5252A` / `#C23636`)
  - 🟡 **Matte Warm Ochre Yellow** (`#E2B438`)
  - 🔵 **Matte Cobalt Slate Blue** (`#2A5288`)
  - ⚪ **Matte Chalk White** (`#EAE6E1`)
  - ⬛ **Graphite Dark & AMOLED Deep Black** (`#000000`)
- **Dynamic Accent Popups**: Long-presses and keystroke bubble previews dynamically inherit the exact tint of the underlying functional key.
- **NDot & Inter Typography**: Dot-matrix headings paired with clean, readable Latin and Bengali fonts.

### 🇧🇩 2. Complete Bengali Typing Suite (Avro, Probhat & Jatiyo)
- **Avro Phonetic (অভ্র)**: Seamless phonetics (`ami` → `আমি`, `kemon` → `কেমন`) with instant character rendering and intelligent suggestions.
- **Probhat (প্রভাত)**: Standard Probhat layout with long-press shifted letter mappings (`দ` → `ধ`, `ূ` → `ঊ`, `ী` → `ঈ`, `র` → `ড়`, `ট` → `ঠ`, `এ` → `ঐ`, `ু` → `উ`, `ি` → `ই`, `ও` → `ঔ`, `প` → `ফ`). No confusing number clutter on letter keys.
- **Jatiyo (জাতীয়)**: Official national standard layout with complete conjunct and vowel support.
- **One-Tap Language Toggle**: Switch between English and Bengali instantly without input lag.

### 🔢 3. Streamlined 3-Page Symbol & Number Architecture
- **Page 1 (Alphabet)**: Clean QWERTY / Bengali layout.
- **Page 2 (Numbers & Standard Symbols)**: Number row, arithmetic signs, and common punctuation.
- **Page 3 (Extended Symbols & Math)**: Advanced math symbols (`~ \` | • √ π ÷ × § Δ`), brackets, and typography accessed via `= \<` with reliable navigation and zero layout kickback.

### 🔒 4. Privacy, Verified in the Code
- **One Permission With One Job**: This build keeps the `INTERNET` permission for exactly one feature — Cloud Translate — used only when you open the Translate action and tap Translate. There is **no microphone permission** and no media-projection permission in this app: the built-in Whisper voice engine, the update checker, announcements, crash reporting, and the hidden screenshot assistant from the original source were all removed from the code (see `CUSTOM_BUILD_NOTES.md`). Voice typing works by handing over to your phone's system voice keyboard when you tap the mic action — that service listens under its own permission, and no audio ever passes through this app.
- **On-Device Typing**: Your passwords, messages, bank details, and personal dictionaries never leave your local storage. The only text that ever leaves the phone is text you deliberately type into the Translate box and send yourself.
- **No Telemetry**: No trackers, no analytics SDKs, and no background crash beacons.

### ⚡ 5. Power Features
- **Haptic Engine**: Crisp, tactile vibration feedback fine-tuned for modern linear haptic motors.
- **Clipboard Management**: Quick clipboard history with pinned items.
- **Customizable Dimensions**: Adjustable keyboard height, one-handed mode, and split tablet layouts.
- **Gesture Typing**: Smooth glide trace input with real-time word formulation.

---

## 📸 Screenshots & Aesthetics

```
+-------------------------------------------------------+
|  [ Q ] [ W ] [ E ] [ R ] [ T ] [ Y ] [ U ] [ I ] [ O ] [ P ]  |
|   [ A ] [ S ] [ D ] [ F ] [ G ] [ H ] [ J ] [ K ] [ L ]   |
|    [^]   [ Z ] [ X ] [ C ] [ V ] [ B ] [ N ] [ M ]   [x]   |
|   [?123]   [  🌐  ]   [     Space Bar     ]   [ . ]  [ ↵ ]   |
+-------------------------------------------------------+
```

---

## 🛠️ Tech Stack & Architecture

- **Operating System:** Android 7.0 (Nougat, API 24) through Android 15 (Vanilla Ice Cream, API 35)
- **Architecture:** Android Open Source Project (AOSP) LatinIME foundation with modern Jetpack Compose UIX
- **Native Core:** C++ JNI Native Input Engine (`libjni_latinime.so`, pocketfft, ggml)
- **Languages:** Java, Kotlin, C++
- **Build System:** Gradle 8.2 & Android Gradle Plugin 8.3

---

## 🚀 Building from Source

### Prerequisites
- Android Studio Ladybug / Koala or CLI tools
- Java Development Kit (JDK) 17+
- Android SDK 34 & NDK (`r25c` or newer)

### Build Steps
```bash
# Clone the repository
git clone <this repository>
cd <repository root>

# Build the release APKs
./gradlew assembleStableRelease

# Outputs are located in:
# build/outputs/apk/stable/release/
```

---

## 🐛 Bug Reporting & Feedback

We actively maintain Leaf Board and appreciate your reports, suggestions, and feedback.

If you encounter any bug, glitch, or layout suggestion:
- **Issue Tracker:** use the Issues tab of this repository

When reporting a bug, please include:
1. Device model (e.g., Nothing Phone 2a, Pixel 8)
2. Android version
3. Steps to reproduce the issue

---

---

## 📄 License & Legal Notice

Leaf Board is released under the **Apache License 2.0**.

```text
Upstream copyright notices are preserved in LICENSE.md and NOTICE.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

See [LICENSE.md](LICENSE.md) and [NOTICE](NOTICE) for complete third-party licenses and credits.

---

### 🔍 Search Tags & SEO Keywords
`Leaf Board` `Leaf Board Keyboard` `Bengali Keyboard Android` `Probhat Keyboard` `Avro Keyboard` `Jatiyo Keyboard Android` `Privacy Keyboard Android` `Offline Keyboard APK`
