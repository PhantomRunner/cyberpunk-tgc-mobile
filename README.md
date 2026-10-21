# Cyberpunk TCG Companion

A small, offline-first Android college project for managing a physical card
collection and saved decks. The current scaffold displays a single Compose
screen. Collection, decks, navigation, photography, OCR and sensors are planned
in later tasks.

## Build and run

Requirements:

- JDK 17 (set `JAVA_HOME` to its installation directory).
- Android SDK Platform 36 and SDK Build Tools 35.0.0, installed through Android
  Studio's SDK Manager or Android command-line tools.
- Network access for the first Gradle/dependency download. The app itself does
  not need network access.

Open this repository's root directory in Android Studio, choose JDK 17 as the
Gradle JDK, and let Gradle sync finish. Set the SDK location in Android Studio,
or set `ANDROID_HOME` to the SDK directory. Alternatively, create an untracked
`local.properties` containing `sdk.dir=/absolute/path/to/your/android-sdk`.

From the repository root:

```sh
./gradlew assembleDebug
```

On Windows, use `gradlew.bat assembleDebug`. The wrapper downloads the pinned
Gradle version; a separate Gradle installation is unnecessary.

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
Run the `app` configuration in Android Studio on an emulator or device with
Android 7.0 (API 24) or newer. With a connected device, `./gradlew installDebug`
installs the APK; open **Cyberpunk TCG Companion** from the launcher.

## Project setup

There is one `app` module with Kotlin sources under
`app/src/main/java/com/example/cyberpunktcg`. It uses Android Gradle Plugin
8.13.0, Gradle 8.13, Kotlin/Compose compiler plugin 2.2.10, Compose BOM
2025.08.00 and Activity Compose 1.10.1. The BOM keeps Compose UI and Material 3
versions aligned; Activity Compose hosts the Compose screen. Java and Kotlin
bytecode target Java 17. Compile/target SDK is 36; minimum SDK is 24.

The manifest declares only the launcher activity, with no permissions. There
are no external images or proprietary card assets. The screen uses the default
Material 3 styling; the custom application theme belongs to M1-02.

Build output, SDK paths, IDE files and signing secrets are ignored by Git.
Keep machine-specific configuration out of committed files.
