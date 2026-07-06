# Expense Tracker (Android)

A personal, offline-first expense tracker for Android (Java + XML). See
[`docs/prd.md`](docs/prd.md) for product requirements and
[`docs/lld.md`](docs/lld.md) for the technical design.

This README explains how to **build the app** and **run its tests on an Android
emulator**.

---

## 1. Project at a glance

| Item | Value |
| --- | --- |
| Application id / namespace | `com.hitstudio.expensetracker` |
| Build system | Gradle (Kotlin DSL) with the wrapper |
| Gradle version | `9.4.1` (via `./gradlew`) |
| Android Gradle Plugin | `9.2.1` |
| `compileSdk` / `targetSdk` | `36` |
| `minSdk` | `31` (Android 12) |
| Language level | Java 11 |
| Instrumentation runner | `androidx.test.runner.AndroidJUnitRunner` |

Test source sets:

- `app/src/test/` — **unit tests** that run on the host JVM (no emulator needed).
- `app/src/androidTest/` — **instrumented tests** that run on an emulator or a
  physical device.

---

## 2. Prerequisites

1. **JDK 17 or newer** (JDK 21 LTS recommended; AGP 9.x requires JDK 17+).
   Android Studio ships with a compatible JBR, so installing Android Studio is
   the simplest option.
2. **Android SDK** with, at minimum:
- Platform `android-36`
- `platform-tools` (provides `adb`)
- `emulator`
- A **system image** at API level **31 or higher** (must be `>= minSdk`).
3. **An Android emulator (AVD)** — see [§5](#5-set-up-and-launch-an-android-emulator).

> The SDK location is read from `local.properties` (`sdk.dir`). On this machine
> it is `~/Library/Android/sdk`.

### Optional: expose the SDK tools on your `PATH`

Running emulator/SDK commands from a terminal is easier if the tools are on your
`PATH`. Add this to `~/.zshrc`:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
```

Then reload: `source ~/.zshrc`.

---

## 3. Quick start

```bash
# 1. Build the debug APK
./gradlew assembleDebug

# 2. Start an emulator (see §5 to create one first)
emulator -avd expense_test &

# 3. Wait for it to come online, then run everything
adb wait-for-device
./gradlew check connectedCheck
```

`check` runs the host unit tests; `connectedCheck` runs the instrumented tests
on the running emulator.

---

## 4. Build the app

All commands are run from the project root. Use `./gradlew` on macOS/Linux and
`gradlew.bat` on Windows.

```bash
# Compile and assemble the debug APK
./gradlew assembleDebug

# Assemble the (signed) release APK
./gradlew assembleRelease

# Remove build outputs
./gradlew clean

# List every available task
./gradlew tasks
```

### 4.1 APK outputs

The APK base name is set via `base.archivesName` (`ExpenseTracker`) in
`app/build.gradle.kts`. The release variant additionally drops the build-type
suffix so the shippable artifact is simply `ExpenseTracker.apk`.

| Variant | Command | Output file | Signed with |
| --- | --- | --- | --- |
| Debug | `./gradlew assembleDebug` | `app/build/outputs/apk/debug/ExpenseTracker-debug.apk` | Auto debug key |
| Release | `./gradlew assembleRelease` | `app/build/outputs/apk/release/ExpenseTracker.apk` | Release key (see [§4.2](#42-release-signing)) |

Build **and install** onto a running emulator/device:

```bash
# Debug
./gradlew installDebug

# Release (requires signing — see §4.2)
./gradlew installRelease

# Or install an already-built APK directly
adb install -r app/build/outputs/apk/release/ExpenseTracker.apk
```

### 4.2 Release signing

Android refuses to install an **unsigned** APK. Debug builds are auto-signed with
a local debug key, but **release** builds need their own keystore. The build
reads signing credentials from a git-ignored `keystore.properties` at the
project root; without that file the release build still compiles, just unsigned.

**One-time setup:**

1. Generate a keystore (10,000-day validity is the Play Store minimum):

```bash
keytool -genkeypair -v \
 -keystore release.jks -storetype PKCS12 \
 -alias expensetracker -keyalg RSA -keysize 2048 -validity 10000 \
 -dname "CN=Expense Tracker, OU=Mobile, O=Expense Tracker, C=IN"
```

2. Create `keystore.properties` in the project root (already git-ignored):

```properties
storeFile=release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=expensetracker
keyPassword=YOUR_KEY_PASSWORD
```

3. Build and verify the signature:

```bash
./gradlew assembleRelease
apksigner verify --print-certs app/build/outputs/apk/release/ExpenseTracker.apk
```

> **Back up `release.jks` and its passwords** somewhere safe (e.g. a password
> manager). They are intentionally **not** committed to git. If you publish to
> Google Play and later lose this key, you cannot ship updates under the same
> app identity.

### 4.2.1 Repository hygiene before release

Before pushing a GitHub release, verify that only source files, docs, and
tracked build files are present:

- Keep `local.properties`, `keystore.properties`, `*.jks`, `*.keystore`, and
  `google-services.json` out of git.
- Keep the Gradle wrapper files (`gradlew`, `gradlew.bat`, and
  `gradle/wrapper/*`) committed so a clean clone can build without a local
  Gradle install.
- Check `git status --short --ignored` before tagging a release to confirm that
  no local secrets or machine-specific files are staged.

### 4.3 Google Play release (App Bundle)

Google Play requires an **Android App Bundle (`.aab`)** — not an APK — for new
apps. The APK targets in §4.1 are for local install / sideloading and testing
only.

```bash
# Build the signed release App Bundle (the Play upload artifact)
./gradlew bundleRelease
```

| Artifact | Command | Output file | Upload to Play? |
| --- | --- | --- | --- |
| App Bundle | `./gradlew bundleRelease` | `app/build/outputs/bundle/release/ExpenseTracker-release.aab` | **Yes** |
| Release APK | `./gradlew assembleRelease` | `app/build/outputs/apk/release/ExpenseTracker.apk` | No (local install / testing) |

> Enroll in **Play App Signing**: upload the `.aab` signed with the key from
> §4.2 as your *upload key*, and let Google manage the final *app signing key*.
> This is required for new apps and lets you reset the upload key if it is lost.

---

## 5. Set up and launch an Android emulator

You can use **Android Studio** (easiest) or the **command line**.

### Option A — Android Studio

1. Open the project in Android Studio.
2. Go to **Tools ▸ Device Manager** (or the Device Manager icon).
3. Click **Create Device**, pick a hardware profile (e.g. *Pixel 7*).
4. Select a **system image** with API level **31+** (e.g. API 36) and download it.
5. Finish, then press **▶ (Launch)** to start the emulator.

### Option B — Command line

Pick a system image that matches your machine's architecture:
`arm64-v8a` for Apple Silicon (M-series), `x86_64` for Intel Macs.

```bash
# Install the required SDK packages (example uses Apple Silicon image)
sdkmanager "platform-tools" "emulator" "platforms;android-36" \
 "system-images;android-36;google_apis;arm64-v8a"

# Create an AVD named "expense_test"
avdmanager create avd -n expense_test \
 -k "system-images;android-36;google_apis;arm64-v8a" \
 --device "pixel_7"

# Launch it
emulator -avd expense_test

# List available AVDs at any time
emulator -list-avds
```

### Confirm the emulator is connected

In another terminal:

```bash
adb devices
```

You should see an `emulator-5554   device` entry. Gradle's instrumented-test
tasks require at least one such online device.

---

## 6. Run the tests

### 6.1 Unit tests (host JVM — no emulator)

These live in `app/src/test/` and run fast on your machine.

```bash
# All unit tests
./gradlew test

# Just the debug variant
./gradlew testDebugUnitTest

# A single class
./gradlew testDebugUnitTest --tests "com.hitstudio.expensetracker.ExampleUnitTest"

# A single method
./gradlew testDebugUnitTest --tests "com.hitstudio.expensetracker.ExampleUnitTest.addition_isCorrect"
```

### 6.2 Instrumented tests (on the emulator)

These live in `app/src/androidTest/` and need a **running emulator or device**.
Start one first (see [§5](#5-set-up-and-launch-an-android-emulator)), then:

```bash
# Run all instrumented tests on the connected emulator
./gradlew connectedDebugAndroidTest

# Equivalent across all testable variants
./gradlew connectedAndroidTest
```

Run a single instrumented test class or method with the runner arguments:

```bash
# One class
./gradlew connectedDebugAndroidTest \
 -Pandroid.testInstrumentationRunnerArguments.class=com.hitstudio.expensetracker.ExampleInstrumentedTest

# One method
./gradlew connectedDebugAndroidTest \
 -Pandroid.testInstrumentationRunnerArguments.class=com.hitstudio.expensetracker.ExampleInstrumentedTest#useAppContext
```

### 6.3 Run everything

```bash
# Host unit tests
./gradlew check

# Instrumented tests on the emulator
./gradlew connectedCheck
```

### 6.4 From Android Studio

- Click the green ▶ gutter icon next to any test class or method.
- Right-click `app/src/test` or `app/src/androidTest` and choose **Run Tests**.
- Instrumented tests prompt you to select a running emulator/device.

---

## 7. Where to find test reports

After a run, open the generated HTML reports in a browser:

| Test type | Report path |
| --- | --- |
| Unit tests | `app/build/reports/tests/testDebugUnitTest/index.html` |
| Instrumented tests | `app/build/reports/androidTests/connected/debug/index.html` |

XML results (useful for CI) are under `app/build/test-results/`.

---

## 8. Common Gradle tasks

| Task | Purpose |
| --- | --- |
| `./gradlew assembleDebug` | Build the debug APK |
| `./gradlew installDebug` | Build + install on emulator/device |
| `./gradlew test` | Run host unit tests |
| `./gradlew connectedDebugAndroidTest` | Run instrumented tests on emulator |
| `./gradlew check` | Lint + host unit tests |
| `./gradlew connectedCheck` | All device/emulator checks |
| `./gradlew clean` | Delete build outputs |
| `./gradlew tasks` | List all tasks |

---

## 9. Troubleshooting

- **`No connected devices!`** — Start an emulator and confirm it appears in
  `adb devices` before running `connected*` tasks.
- **`INSTALL_FAILED_OLDER_SDK` / install errors** — Your emulator's API level is
  below `minSdk` (31). Recreate the AVD with API 31+.
- **Emulator won't boot or is very slow** — Use a system image matching your CPU
  (`arm64-v8a` on Apple Silicon, `x86_64` on Intel) and enable hardware
  acceleration.
- **`SDK location not found`** — Ensure `local.properties` has
  `sdk.dir=/path/to/Android/sdk`, or set `ANDROID_HOME`.
- **Wrong Java version** — Build with JDK 17+. Check with `java -version`, or set
  the project JDK in Android Studio under **Settings ▸ Build Tools ▸ Gradle**.
- **Reset a stuck adb connection** — `adb kill-server && adb start-server`.
- **`Could not GET … repo.maven.apache.org` / dependency downloads fail** — You
  are likely behind a corporate proxy. Gradle (the JVM) ignores the shell's
  `http_proxy`/`https_proxy` vars, so set `systemProp.http.proxyHost` /
  `systemProp.http.proxyPort` (and the `https` equivalents) in
  `~/.gradle/gradle.properties`.
- **`jlink … does not exist` / `JdkImageTransform` fails** — Gradle picked an
  IDE-bundled JRE (which has no `jlink`) instead of a full JDK. Point it at a
  real JDK: set `JAVA_HOME` to a JDK 17/21, or add
  `org.gradle.java.installations.auto-detect=false` plus
  `org.gradle.java.installations.paths=/path/to/jdk` in
  `~/.gradle/gradle.properties`.
