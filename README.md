# Practice 8: Permissions Lab

**Student:** Practice Class Work Submission  
**Package Name:** `uz.ttpu.permissionslab`  
**Lectures & Topics:** Install-time, Runtime, Special Permissions, Jetpack Compose (`LifecycleEventEffect`), API Level Differences (API 23, API 30, API 33+).

---

## Overview

This project implements all required tasks for **Practice 8: Permissions** in Jetpack Compose following modern Android permissions architecture best practices.

### Project Tasks Summary

1. **Task 1: Kotlin Permission Model Simulation (`Task1PermissionModel.kt`)**
   - Pure Kotlin state machine implementing slide 13 permission states (`granted`, `rationale`, `dialogShows`).
   - Verified via standalone `main()` runner and unit tests (`Task1PermissionModelTest.kt`).
2. **Task 2: Normal Permissions & Network Monitor (`Task2NormalPermissions.kt`)**
   - Declares `INTERNET` and `ACCESS_NETWORK_STATE` in `AndroidManifest.xml`.
   - Real-time `ConnectivityManager` state flow showing Online / Offline status without runtime dialogs.
3. **Task 3: Camera Runtime Permission (`Task3CameraScreen.kt`)**
   - Complete 3-state flow: Granted, Rationale Needed, and Denied / Permanently Denied (2x).
   - Shows "Open App Settings" fallback button when permanently denied (`Settings.ACTION_APPLICATION_DETAILS_SETTINGS`).
   - Automatically refreshes state on `ON_RESUME`.
4. **Task 4: Special Permission: Overlay (`Task4OverlayScreen.kt`)**
   - Manages `SYSTEM_ALERT_WINDOW` permission.
   - Interactive `Switch` opening `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` with package URI.
   - Uses `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` to re-evaluate `Settings.canDrawOverlays(context)` when returning from System Settings.
5. **Task 5: One-Time Location Permission (`Task5LocationScreen.kt`)**
   - Uses `ActivityResultContracts.RequestMultiplePermissions()` for `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`.
   - Reads `Map<String, Boolean>` to show "Precise", "Approximate", or "Denied / Revoked" state.
   - Uses `ON_RESUME` lifecycle re-check to detect when "Only this time" permission is revoked after sending app to background.
6. **Task 6 (Bonus): Notification Permission (`Task6NotificationScreen.kt`)**
   - Guarded runtime request for `POST_NOTIFICATIONS` on API 33+ (`Build.VERSION.SDK_INT >= 33`).
   - On API < 33, skips runtime request and allows sending notifications directly.
   - Triggers test notifications with `NotificationManagerCompat`.

---

## Tested API Levels & Device Configurations

- **API Level 30 (Android 11 - x86_64 Emulator):**
  - Verified Task 3 (2x denial -> Settings redirection), Task 4 (Overlay Switch sync), and Task 5 ("Only this time" one-time location revocation).
- **API Level 34 (Android 14 - API 34 Emulator):**
  - Verified Task 5 location dialogs (Precise vs Approximate option), Task 6 `POST_NOTIFICATIONS` runtime prompt, and test notification delivery on Android 14.
- **API Level 32 (Android 12L - API 32 Emulator):**
  - Verified Task 6 notification permission behavior on API < 33 where no runtime prompt is shown.

---

## Observation from Task 5 (One-Time Location)

> **Task 5 Observation Paragraph:**  
> When testing Task 5 on Android 11+ (API 30+), selecting the **"Only this time"** option grants location permission temporarily while the app is active in the foreground. When the app is sent to the background (by pressing Home or switching apps) and then resumed, the OS automatically revokes the temporary location access. Because `Task5LocationScreen` utilizes `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` to re-evaluate `getGeneralLocationState(context)` every time the screen resumes, the UI immediately updates from *"Precise/Approximate Granted"* to *"Location Denied / Revoked"* upon return. This proves that caching "granted" state statically in memory is insecure and that re-evaluating runtime permissions on `ON_RESUME` is mandatory for temporary one-time permissions.

---

## Comparison of Task 6 Behavior Across API Levels

- **API 32 (Android 12L and below):**
  - `POST_NOTIFICATIONS` is an install-time permission. The runtime check `checkNotificationPermission()` evaluates to `true` automatically without displaying any system permission dialog. Users can send notifications immediately.
- **API 34 (Android 14 and API 33+):**
  - `POST_NOTIFICATIONS` requires explicit runtime permission consent. The app displays "Notification Permission Denied" initially with a "Request Notification Permission" button. Tapping the button launches the OS system prompt. Sending test notifications is only enabled after the user approves the prompt.

---

## Part A Answers

All 19 theory questions are answered in detail in [PartA_Answers.md](PartA_Answers.md).

---

## Running Unit Tests & Simulations

To run the Task 1 model simulation in command line or test suite:
```bash
./gradlew testDebugUnitTest
```
Or launch `Task1PermissionModel.kt` `main()` or select **Task 1: Model** tab in the running Android app!

---

## Screenshots

- **Task 3: Camera Granted State:** `screenshots/task3_camera_granted.png`
- **Task 3: Camera Rationale State:** `screenshots/task3_camera_rationale.png`
- **Task 3: Camera Denied 2x (Settings):** `screenshots/task3_camera_settings.png`
- **Task 4: Overlay Permission Switch:** `screenshots/task4_overlay_switch.png`
- **Task 5: One-Time Location:** `screenshots/task5_location.png`
