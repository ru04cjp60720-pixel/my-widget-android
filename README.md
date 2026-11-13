# My Widget Android

A customizable Android home-screen shortcut widget.

## Current prototype

- Add, edit, and delete URL shortcuts in the app
- Save shortcut data locally on the device
- Show saved shortcuts in the home-screen widget
- Tap a widget shortcut to open its URL
- Resizable widget dimensions (subject to launcher support)

## Planned features

- Launch installed apps
- Custom image covers for buttons
- Shortcut groups
- JSON import and export
- Traditional Chinese interface with dark and light themes

## Technology

- Kotlin
- Jetpack Compose for the management UI
- Jetpack Glance for the home-screen widget
- Android SharedPreferences for the current local prototype storage

## Build

The GitHub Actions workflow builds a debug APK on pushes and pull requests to `main`.
The resulting APK is uploaded as the `my-widget-debug-apk` workflow artifact when a build succeeds.

The project currently targets Android 15 (API 35) and supports Android 8.0 (API 26) or later.

## Status

Early prototype. Source files and an automated debug-build workflow are in place. CI build success confirms compilation and APK packaging only; runtime behavior still needs to be tested on Android devices and launchers.
