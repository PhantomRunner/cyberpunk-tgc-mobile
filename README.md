# Cyberpunk TCG Companion

A small, offline-first Android college project for managing a physical card
collection and saved decks. The current scaffold displays a single Compose
screen. Collection, decks, navigation, photography, OCR and sensors are planned
in later tasks.

## Jumpstart: run with Android Studio

1. Install Android Studio and a JDK 17 distribution, then open this repository's
   root folder (the one containing `settings.gradle.kts`).
2. In Android Studio's **SDK Manager**, install **Android SDK Platform 36**.
   Under **SDK Tools**, enable **Show Package Details**, select **Android SDK
   Build-Tools 35.0.0**, and install **Android SDK Platform-Tools**. For an
   emulator, also install **Android Emulator** and a system image.
3. In the IDE settings, find **Gradle JDK** and select your JDK 17 installation.
   Let Gradle sync finish. The included wrapper downloads Gradle and the project
   dependencies, so the first sync needs internet access. The app runs offline.
4. Choose a target running **Android 7.0 (API 24) or newer**:
   - **Emulator:** in **Device Manager**, create a virtual phone using the
     installed system image, then start it.
   - **Physical phone:** enable Developer options and USB debugging, connect
     the phone by USB, and accept its debugging authorization prompt.
5. Select the **app** run configuration and your device, then click **Run**.
   The app should open to a screen displaying **Cyberpunk TCG Companion**.

## Build and run from a terminal

Install the same JDK and SDK packages listed above. On Linux or macOS, replace
the example paths below with your actual installations. The SDK location is
shown in Android Studio's SDK Manager.

```sh
cd /path/to/cyberpunk-tgc
export JAVA_HOME="/path/to/jdk-17"
export ANDROID_HOME="/path/to/android-sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"

java -version
./gradlew assembleDebug
```

`java -version` should report Java 17. A successful build ends with
`BUILD SUCCESSFUL` and writes the APK to
`app/build/outputs/apk/debug/app-debug.apk`. Building an APK does not require
a connected device or running emulator. A separate Gradle installation is
unnecessary.

To install and launch it, start an emulator or connect an authorized phone as
described above. With exactly one device connected:

```sh
adb devices
./gradlew installDebug
adb shell am start -n com.example.cyberpunktcg/.MainActivity
```

In `adb devices`, the target should have status `device`. You can also open
**Cyberpunk TCG Companion** from the device's app launcher after installation.

On Windows, set `JAVA_HOME` and `ANDROID_HOME` in your environment settings and
add their `bin` and `platform-tools` directories respectively to `Path`. Open a
new terminal in the repository root; in PowerShell, use
`.\gradlew.bat assembleDebug` and `.\gradlew.bat installDebug`. The `adb`
commands are the same.

## Common setup fixes

- **Java missing or wrong version:** check `JAVA_HOME` and `java -version`.
  Android Studio's Gradle JDK is configured separately from the terminal.
- **SDK location not found or wrong SDK selected:** set `ANDROID_HOME`, or
  create an untracked `local.properties` in the repository root with
  `sdk.dir=/absolute/path/to/your/android-sdk`. An existing `sdk.dir` takes
  precedence over `ANDROID_HOME`; update it if it points to a different SDK.
- **Missing SDK packages or licenses:** use SDK Manager to install Platform 36
  and Build-Tools 35.0.0 and accept the displayed license agreements.
- **No device or `unauthorized`:** start the emulator, or unlock the USB-connected
  phone and accept its debugging prompt. Check again with `adb devices`.
- **`Permission denied` for `gradlew` on Linux/macOS:** run `chmod +x gradlew`.

## Project setup

There is one `app` module with Kotlin sources under
`app/src/main/java/com/example/cyberpunktcg`. It uses Android Gradle Plugin
8.13.0, Gradle 8.13, Kotlin/Compose compiler plugin 2.2.10, Compose BOM
2025.08.00 and Activity Compose 1.10.1. The BOM keeps Compose UI and Material 3
versions aligned; Activity Compose hosts the Compose screen. Java and Kotlin
bytecode target Java 17. Compile/target SDK is 36; minimum SDK is 24.

The manifest declares only the launcher activity, with no permissions. There
are no external images or proprietary card assets. The Material 3 application
theme follows the device's light/dark setting, using charcoal surfaces and cyan
accents in dark mode and pale surfaces with deeper teal accents in light mode.
It uses built-in fonts: monospace titles and readable sans-serif body text.
Change the emulator's system dark theme setting to check both palettes.

Build output, SDK paths, IDE files and signing secrets are ignored by Git.
Keep machine-specific configuration out of committed files.
