# Bladder GUI V2.1

Bladder GUI V2.1 is the Android smartphone application for a wireless, battery-free implantable bladder volume sensor patch. The app communicates with the patch through an RF430FRL15XH transponder, reads bladder sensor data, plots bladder volume trends, and supports exporting the collected data from a smartphone.

## Features

- NFC-based communication with the RF430FRL152H transponder.
- Readout workflow for bladder sensor measurements.
- Bladder-volume plotting on an Android smartphone.
- Data export support for downstream analysis.
- Bladder-volume-oriented user interface.
- AndroidPlot-based data visualization.

## Project Details

- Application name: Bladder GUI 2.1
- Android package: `com.ti.nfcdemo`
- Version name: `2.1`
- Version code: `21`
- Minimum SDK: 19
- Target SDK: 34
- Language/UI stack: Java and Android XML layouts

## Repository Layout

- `app/src/main/java/`: application source code and NFC helper classes.
- `app/src/main/res/`: layouts, drawables, strings, menus, and NFC tech filters.
- `app/build.gradle`: Android app module configuration.
- `settings.gradle`: Gradle project name and module list.

Generated build outputs, Android Studio local state, APKs, and machine-specific SDK paths are intentionally ignored by Git.

## Build

1. Install Android Studio.
2. Open this folder as an existing Android project.
3. Let Android Studio sync the Gradle project.
4. Build or run the `app` module on an NFC-capable Android device.

Command-line build, when the Android SDK and Gradle environment are configured:

```powershell
.\gradlew :app:assembleDebug
```

## NFC Requirement

The app requires an Android smartphone with NFC support. The manifest declares `android.hardware.nfc` as required.

## License

This project is licensed under the Apache License 2.0. See the LICENSE file for details.
