# Maharashtra Bands MVP

## Overview
A Kotlin Android MVP app for listing local bands, collecting public submissions, and enabling admin moderation with Firebase.

## Setup steps
1. Clone the repository.
2. Open in Android Studio.
3. Set Android SDK to `minSdk 24`, `compileSdk 34`, `targetSdk 34`.
4. Sync Gradle.

## Firebase configuration
1. Create a Firebase project and register the Android app.
2. Download `google-services.json` and place it in `app/`.
3. Enable Firebase services:
   - Authentication (Email/Password)
   - Firestore
   - Storage
4. Apply Firebase Gradle plugins and dependencies as described in `SETUP.md`.
5. Enable Firestore offline persistence in your `Application` class.
6. Add Firestore/Storage security rules from `firestore.rules` and `storage.rules`.
7. Create admin users and assign a custom claim `admin: true`.

## Build & run
1. Build: **Build > Make Project** or `./gradlew assembleDebug`.
2. Run on a device/emulator from Android Studio.

## Notes
- Public users can browse approved bands and submit new entries.
- Admins can approve submissions and manage bands.
- See `FINAL_CHECKLIST.md` for final QA steps and common bugs to avoid.
