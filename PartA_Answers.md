# Part A: Theory Questions & Answers

### Question 1
**Context:** A student wants to add photo capture to an app. They immediately wrote the `CAMERA` permission into the manifest and moved on to the dialog code. A teammate says: "First decide whether a permission is needed at all."

**Task:** List the four decisions of the permission workflow from the lecture, in order. Give an example of a case that works without any permission, and explain why that is the best option.

**Answer:**
1. **The Four Decisions of the Permission Workflow (in order):**
   - **Step 1: Evaluate necessity.** Determine if the feature actually requires declaring a runtime permission or if it can be accomplished without permissions (e.g., using system intents or picker APIs).
   - **Step 2: Classify permission type.** Check if the permission is install-time (normal/signature), runtime (dangerous), or special.
   - **Step 3: Check runtime status.** Before accessing the protected resource, check if the permission has already been granted using `checkSelfPermission()`.
   - **Step 4: Handle rationale & user choice.** If permission was denied previously, present an educational explanation (rationale) and handle the user's decision (granted vs permanently denied).

2. **Example of working without permission:**
   - Capturing a photo using system intents (`MediaStore.ACTION_IMAGE_CAPTURE` or Compose `ActivityResultContracts.TakePicture()`), or selecting photos using the Android Photo Picker (`PickVisualMedia`).

3. **Why working without permission is the best option:**
   - It significantly reduces user friction and privacy concerns because the user remains in complete control within a trusted system app. It also eliminates unnecessary manifest declarations and runtime dialog boilerplate code.

---

### Question 2
**Context:** Your teammate thinks Android permissions are just "one line in the manifest" and writes the same code for every permission.

**Task:** Name the three permission categories (install-time, runtime, special) and explain how each one is granted. Show why the category decides how much code you have to write.

**Answer:**
1. **Install-Time Permissions (Normal & Signature):**
   - **How granted:** Granted automatically by the system when the app is installed or updated.
   - **Code required:** Only one line in `AndroidManifest.xml` (`<uses-permission>`). Zero Kotlin runtime check/dialog code needed.

2. **Runtime Permissions (Dangerous):**
   - **How granted:** Granted dynamically at runtime when the user explicitly accepts a system permission dialog prompt.
   - **Code required:** Manifest entry + runtime permission checks (`checkSelfPermission`), rationale display logic (`shouldShowRequestPermissionRationale`), launching permission requests, and handling results.

3. **Special Permissions (e.g., `SYSTEM_ALERT_WINDOW`, `WRITE_SETTINGS`):**
   - **How granted:** Granted manually by the user via a dedicated System Settings page.
   - **Code required:** Manifest entry + explicit status check APIs (e.g., `Settings.canDrawOverlays`), launching an Intent to System Settings with package URI (`package:${context.packageName}`), and observing activity lifecycle events (`ON_RESUME`) to refresh UI state upon return.

---

### Question 3
**Context:** A developer added `INTERNET` to the manifest and is now writing a `checkSelfPermission` check before every network request. In another app the same line was forgotten.

**Task:** Explain why a normal permission needs no runtime check and no dialog. What exception does the app throw when the manifest line is forgotten? Give the three examples of normal permissions from the lecture.

**Answer:**
1. **Why no runtime check/dialog is needed:**
   - Normal permissions present minimal risk to user privacy, device security, or user data. Therefore, the Android OS automatically approves them at installation time.

2. **Exception thrown if manifest line is forgotten:**
   - `java.lang.SecurityException: Permission denied (missing INTERNET permission)`.

3. **Three examples of normal permissions:**
   - `android.permission.INTERNET`
   - `android.permission.ACCESS_NETWORK_STATE`
   - `android.permission.ACCESS_WIFI_STATE`

---

### Question 4
**Context:** A company has two apps: "Bank" and "Wallet". Wallet wants to bind to the bank's `AccountsService`, which is protected by the permission `uz.ttpu.bank.permission.READ_ACCOUNTS`. A third-party app requests the same permission.

**Task:** Who is a signature permission granted to? Which `protectionLevel` is written when you declare it in the manifest? What is the result for Wallet and for the stranger app, and is a dialog shown to the user?

**Answer:**
1. **Who is a signature permission granted to:**
   - Granted strictly to apps signed with the exact same developer certificate as the app that declared the permission.

2. **`protectionLevel` written in manifest:**
   - `android:protectionLevel="signature"`

3. **Result for Wallet vs. stranger app:**
   - **Wallet App:** Automatically granted the permission at install time because its signing signature matches Bank's signature.
   - **Stranger App:** System refuses to grant the permission because the signing certificates do not match.

4. **Is a dialog shown?**
   - No dialog is ever shown to the user for signature permissions.

---

### Question 5
**Context:** A student believes install-time permissions must also be requested through a dialog while the app is running.

**Task:** Explain when and where install-time permissions are shown to the user, and when the system grants them. Which two kinds of permissions belong to this category?

**Answer:**
1. **When and where shown:**
   - Listed on the app details/store page in Google Play Store prior to downloading and installing the app.

2. **When system grants them:**
   - Automatically upon installation or update of the application on the device.

3. **Two kinds of permissions belonging to this category:**
   - Normal permissions
   - Signature permissions

---

### Question 6
**Context:** An app ships with `minSdk 21`. On an Android 5.1 (API 22) device no dialog appears for `CAMERA`, yet the camera works. On Android 14 the dialog appears.

**Task:** Explain the difference: in which version were runtime permissions introduced, and why is a runtime-type permission granted at install time on API 22? How does the lecture's `granted(kind, sdk)` model express this rule?

**Answer:**
1. **Version introduced:**
   - Runtime permissions were introduced in Android 6.0 (API 23, Marshmallow).

2. **Why granted at install time on API 22:**
   - Prior to API 23, Android granted all permissions listed in `AndroidManifest.xml` during app installation. The user accepted all permissions at once before installing the app.

3. **Lecture's `granted(kind, sdk)` model rule:**
   - `If sdk < 23 -> granted = true for all manifest permissions at install-time.`
   - `If sdk >= 23 AND permission is Runtime -> granted requires dynamic runtime user consent.`

---

### Question 7
**Context:** The user denied the camera permission twice. The developer calls `launch()` again when the button is tapped, but nothing appears on screen and the permission comes back denied immediately.

**Task:** Write the states of a runtime permission in order (not asked yet -> denied once -> denied twice). What is `shouldShowRationale` in each state and what should the code do? Where should the user be sent after two denials?

**Answer:**
1. **Permission States in Order:**
   - **State 1: Not asked yet**
     - `granted = false`, `shouldShowRationale = false`.
     - **Code action:** Launch standard system permission dialog (`launcher.launch()`).
   - **State 2: Denied once**
     - `granted = false`, `shouldShowRationale = true`.
     - **Code action:** Show rationale UI explaining why feature needs permission, plus a button to re-trigger permission request.
   - **State 3: Denied twice (Permanently denied)**
     - `granted = false`, `shouldShowRationale = false`.
     - **Code action:** System dialog is blocked by OS. Code must display an "Open Settings" button directing the user to App Details Settings.

2. **Where to send user after two denials:**
   - Direct user to `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` with URI `package:${context.packageName}`.

---

### Question 8
**Context:** A new teammate is reading the `CameraPermissionExample` composable. It uses `rememberPermissionState`, `@OptIn(ExperimentalPermissionsApi::class)` and a `when` with three branches.

**Task:** Write when each of the three branches (`isGranted`, `shouldShowRationale`, the remaining state) runs and what is shown on screen. Why does `rememberPermissionState` return state that survives recomposition? What does the `ExperimentalPermissionsApi` marker mean?

**Answer:**
1. **Three Branches Execution & UI:**
   - `status.isGranted`: Runs when permission is currently granted. UI shows camera preview and camera feature content.
   - `status.shouldShowRationale`: Runs when user denied permission once previously. UI shows educational rationale text and a re-request button.
   - `Else (Remaining State)`: Runs when permission is not asked yet or permanently denied (denied 2x). UI shows initial request button or "Open Settings" button.

2. **Why survives recomposition:**
   - `rememberPermissionState` uses Jetpack Compose state holders (`remember` / `rememberSaveable`) tied to the activity lifecycle to retain permission status across UI recompositions and configuration changes.

3. **Meaning of `@OptIn(ExperimentalPermissionsApi::class)`:**
   - Signifies that the Accompanist Permissions library API is experimental, undergoing active development, and subject to breaking changes in future library releases.

---

### Question 9
**Context:** The team does not want to add an extra library to the project.

**Task:** Explain how to write the request without a library using `rememberLauncherForActivityResult` and `ActivityResultContracts.RequestPermission()`. Which call reads the current state (`ContextCompat.checkSelfPermission`), and which function tells you whether to show a rationale? Compare this approach with Accompanist.

**Answer:**
1. **Requesting without library:**
   ```kotlin
   val launcher = rememberLauncherForActivityResult(
       contract = ActivityResultContracts.RequestPermission()
   ) { isGranted -> /* Handle grant/deny callback */ }
   ```

2. **Reading current state:**
   - `ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED`

3. **Function for checking rationale:**
   - `ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)`

4. **Comparison with Accompanist:**
   - **Standard Activity Result API:** Native Jetpack solution; zero external dependencies; requires manual state management (`mutableStateOf`) and checking activity lifecycle on resume.
   - **Accompanist:** Declarative wrapper library that combines checks, rationale state, and launchers into a unified `PermissionState` object.

---

### Question 10
**Context:** A map screen asks for precise (`ACCESS_FINE_LOCATION`) and approximate (`ACCESS_COARSE_LOCATION`) location together.

**Task:** Explain what shape the `RequestMultiplePermissions` contract returns (`Map<String, Boolean>`) and how to read it. Why does the user see a single interface for these two permissions? What should the app do if the user denies both?

**Answer:**
1. **Return shape & how to read it:**
   - Returns a `Map<String, Boolean>` where keys are permission string names and values are grant statuses (`true`/`false`).
   - Read via: `permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] == true` for precise, and `permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] == true` for approximate.

2. **Why single interface:**
   - Starting in Android 12 (API 31), Android presents a combined location dialog allowing users to choose between "Precise" vs "Approximate" accuracy in a single dialog for enhanced privacy control.

3. **What app should do if user denies both:**
   - Inform user that location-based features are unavailable, gracefully disable location tracking, and offer a fallback option or retry prompt.

---

### Question 11
**Context:** A developer tries to show a normal runtime dialog for `SYSTEM_ALERT_WINDOW`, but no dialog appears at all.

**Task:** Explain how special permissions differ from ordinary runtime permissions. Complete the table: which call tells you whether each permission has been granted?

| Permission | What it allows | Check call |
|---|---|---|
| `SYSTEM_ALERT_WINDOW` | Drawing overlays on top of other apps | `Settings.canDrawOverlays(context)` |
| `WRITE_SETTINGS` | Modifying system settings | `Settings.System.canWrite(context)` |
| `MANAGE_EXTERNAL_STORAGE` | All files management access | `Environment.isExternalStorageManager()` |
| `REQUEST_INSTALL_PACKAGES` | Requesting app package installation | `context.packageManager.canRequestPackageInstalls()` |
| `ACCESS_BACKGROUND_LOCATION` | Accessing location in the background | `ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED` |

**Explanation:**
- Special permissions govern sensitive system capabilities that can affect system stability or security. They cannot be granted via runtime in-app dialogs; the user must be directed to a dedicated System Settings page.

---

### Question 12
**Context:** The user enabled the overlay permission on the Settings screen and returned to the app, but the app still shows "no permission".

**Task:** Explain the "check -> open Settings -> check again on return" pattern. Why does no callback reach your code when the user returns from Settings? Fix the code using `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)`.

**Answer:**
1. **Why no callback reaches code:**
   - Launching system settings via `startActivity()` with `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` does not return an explicit result callback to `ActivityResultLauncher`.

2. **The "check -> open Settings -> check again on return" pattern:**
   - App checks status on load -> opens system settings if disabled -> re-evaluates permission status in activity `ON_RESUME` when user navigates back to app.

3. **Code Fix:**
   ```kotlin
   @Composable
   fun OverlayScreen() {
       val context = LocalContext.current
       var canDraw by remember {
           mutableStateOf(Settings.canDrawOverlays(context))
       }

       LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
           canDraw = Settings.canDrawOverlays(context)
       }

       Switch(
           checked = canDraw,
           onCheckedChange = { openOverlaySettings(context) }
       )
   }
   ```

---

### Question 13
**Context:** A developer requests `ACCESS_FINE_LOCATION` and `ACCESS_BACKGROUND_LOCATION` together in a single launch call. Background location does not work.

**Task:** Explain the two-step order from the lecture: what is requested in step 1, and when and why is step 2 called only after the first one is granted? Why is the second request wrapped in an `SDK_INT >= Build.VERSION_CODES.Q` check?

**Answer:**
1. **Two-step order:**
   - **Step 1:** Request foreground location (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`).
   - **Step 2:** Only after foreground location is granted, trigger a separate request for `ACCESS_BACKGROUND_LOCATION`.

2. **Why step 2 is called separately after step 1:**
   - On Android 11 (API 30)+, requesting foreground and background location simultaneously in one call causes OS to ignore background request or fail immediately.

3. **Why wrapped in `SDK_INT >= Build.VERSION_CODES.Q` check:**
   - `ACCESS_BACKGROUND_LOCATION` permission was introduced in Android 10 (API 29 / Android Q). On Android 9 (API 28) and lower, granting foreground location automatically granted background location access.

---

### Question 14
**Context:** An app requests five permissions together: two location, two contacts and one camera. The student expected five dialogs, but only three appeared.

**Task:** Explain what a permission group is and how the system uses it to reduce the number of dialogs. Give the three group examples from the lecture. Why should your code not rely on group membership?

**Answer:**
1. **Permission Group & Dialog Reduction:**
   - Dangerous permissions belong to logical permission groups (e.g., `LOCATION`, `CONTACTS`, `CAMERA`). If an app requests a permission in a group where another permission has already been granted, OS grants it without prompting an additional dialog.

2. **Three Group Examples:**
   - `Manifest.permission_group.CAMERA`
   - `Manifest.permission_group.CONTACTS`
   - `Manifest.permission_group.LOCATION`

3. **Why code must NOT rely on group membership:**
   - Permission grouping behavior is an OS internal implementation detail that changes across Android releases and manufacturer OEMs. Code must explicitly request every required permission.

---

### Question 15
**Context:** A student wrote the dialog code for `CAMERA` but forgot to add `<uses-permission>` to the manifest.

**Task:** Explain what happens. How does the declaration process differ for install-time and for runtime/special permissions? When is the `android:maxSdkVersion` attribute used (the `WRITE_EXTERNAL_STORAGE` example)?

**Answer:**
1. **What happens:**
   - The OS blocks the runtime request; no permission dialog appears, and calling feature APIs results in a `SecurityException`.

2. **Declaration difference:**
   - ALL permissions must be declared in `AndroidManifest.xml` via `<uses-permission>`. Runtime and special permissions require additional dynamic Kotlin request logic in code.

3. **`android:maxSdkVersion` usage:**
   - Used when a permission is only required up to a specific Android version. Example: `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />`. Starting in API 29/30 (Scoped Storage), modern MediaStore APIs replace legacy storage permissions.

---

### Question 16
**Context:** An app uses the camera optionally. The store hid the app on devices without a camera (for example Android TV), but the team wants the app installable everywhere.

**Task:** Explain how the true and false values of `required` in `<uses-feature android:name="android.hardware.camera" android:required="..." />` affect store filtering. With `required="false"`, what must the app check in code (`hasSystemFeature`)? Interpret the four results of the lecture's `visibleOn` model.

**Answer:**
1. **`required="true"` vs `required="false"` store filtering:**
   - `required="true"`: Play Store filters out and hides the app from devices lacking camera hardware.
   - `required="false"`: App remains installable on all devices regardless of camera hardware presence.

2. **Code check with `required="false"`:**
   - `context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)`

3. **Four Results of `visibleOn` Model:**
   - **Hardware Present + required=true:** Visible on Play Store, feature functional.
   - **Hardware Missing + required=true:** Hidden/filtered on Play Store.
   - **Hardware Present + required=false:** Visible on Play Store, feature functional after `hasSystemFeature` check.
   - **Hardware Missing + required=false:** Visible on Play Store, app gracefully hides/disables camera feature in UI.

---

### Question 17
**Context:** A developer reads the camera state once and stores it. The user had chosen "Only this time". After the app goes to the background and comes back an hour later, the camera button looks enabled but does not work.

**Task:** Explain how a one-time permission (Android 11, API 30) works and when it is revoked. Why should you never cache "granted" forever? Fix the code using the `ON_RESUME` solution from the lecture.

**Answer:**
1. **How One-Time Permission works & when revoked:**
   - Introduced in Android 11 (API 30), "Only this time" grants temporary permission. As soon as the app process is closed, stopped, or backgrounded for a period, OS automatically revokes permission.

2. **Why never cache "granted" forever:**
   - Permission status is volatile and can change asynchronously while the app is in background.

3. **Code Fix:**
   ```kotlin
   @Composable
   fun CameraScreen() {
       val context = LocalContext.current
       var hasCameraPermission by remember {
           mutableStateOf(context.hasCameraPermission())
       }

       LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
           hasCameraPermission = context.hasCameraPermission()
       }

       // Use hasCameraPermission state
   }
   ```

---

### Question 18
**Context:** A user has not used the app for several months. When they return, a permission granted earlier no longer works. On the other hand, in the new version the app no longer needs `READ_CONTACTS`.

**Task:**
(1) Explain what auto-reset (Android 11+) does and what the app should do; how can the user turn it off for the app (`ACTION_AUTO_REVOKE_PERMISSIONS`)?
(2) On Android 13 (API 33), which function removes runtime permissions you no longer need (`revokeSelfPermissionOnKill`), and why is that good for the user?

**Answer:**
1. **Auto-reset & turning off:**
   - On Android 11+, OS automatically revokes runtime permissions for apps unused for a few months. App must handle re-prompting on next open. Users can disable auto-reset by opening `Intent(Intent.ACTION_AUTO_REVOKE_PERMISSIONS, Uri.parse("package:${context.packageName}"))`.

2. **`revokeSelfPermissionOnKill` (Android 13 / API 33):**
   - Calls `Context.revokeSelfPermissionOnKill(Manifest.permission.READ_CONTACTS)`. Allows app to voluntarily surrender previously granted runtime permissions it no longer uses, enforcing principle of least privilege and increasing user trust.

---

### Question 19
**Context:** You are asked to write a short summary of how permissions changed across Android versions.

**Task:** For API 23, API 30 and API 33, write the main change in one sentence each. Explain how `POST_NOTIFICATIONS` became a runtime permission in Android 13 and why you should not request it on versions below API 33.

**Answer:**
1. **Main Version Changes (1 sentence each):**
   - **API 23 (Android 6.0):** Introduced dynamic runtime permissions requiring explicit user consent at runtime for dangerous permissions instead of install-time grants.
   - **API 30 (Android 11):** Introduced one-time ("Only this time") permissions and automatic permission reset for unused apps.
   - **API 33 (Android 13):** Made notification permission (`POST_NOTIFICATIONS`) a runtime requirement and introduced granular media permissions (`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_MEDIA_AUDIO`).

2. **`POST_NOTIFICATIONS` in Android 13 & why not request below API 33:**
   - In API 33+, notifications require explicit user permission prompt. Below API 33, notifications are allowed automatically at install time; calling runtime permission requests on older OS versions is unnecessary and can cause compatibility bugs or runtime errors if not guarded by `SDK_INT >= 33`.
