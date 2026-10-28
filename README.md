# Implicit Intent Demo

A small Android demo of implicit intents, end to end. The host screen sends
an implicit intent to add or view a person; the person screen answers it
with a form or a details view, then returns the person as a result intent.
Neither screen stores anything.

Originally a Full Sail assignment (CE07) built as a single Java activity
that could only be launched by a separate host app; v2.0 adds the host
screen, so the exercise runs standalone, and rewrites both screens in
Kotlin with Compose. See [CHANGELOG.md](CHANGELOG.md) for what changed
and what was deliberately left out.

This public repository is a squashed export of that v2.0.0 modernization:
a single commit, with the earlier private history intentionally not included.

## Tech stack

- Kotlin 2.2.0, Jetpack Compose, Material 3
- ViewModel + StateFlow for the host screen's returned person
- Android Gradle Plugin 8.13.0, Gradle 8.14.3 (via the wrapper), JDK 17
- compile/target SDK 36, minSdk 24
- JUnit 4, Compose UI tests

The application ID is `com.enonymous1.implicitintentdemo`.

## How it works

- `MainActivity` is the launcher host. Add and View send the implicit
  intents `com.enonymous1.implicitintentdemo.ACTION_ADD_PERSON` and
  `...ACTION_VIEW_PERSON`, and receive the result through the Activity
  Result API. The returned person is kept only in the host `ViewModel`
  (surviving rotation and process death via `SavedStateHandle`) until it
  is deleted or replaced.
- `PersonActivity` is the exported callee. Its intent filter also lists
  the original Full Sail actions, so the CE08 host that launched v1.0 can
  still reach it; both the current and the original extra names are
  accepted on the way in, and results always carry the current names.
  A launch with no recognised action, or a view intent with no usable
  person, shows an unsupported state instead of crashing.
- The form never saves anything itself, and the details screen never
  deletes anything itself. Both return the person as a result intent,
  exactly as the original did. Backing out returns no result.
- Validation is pure Kotlin in `PersonContract.kt`: names are trimmed,
  and the age must be a whole number from 0 to 150. Bad input gets one
  clear inline message instead of a Toast, and an overflowing age no
  longer crashes the app.

## Features

- Add a person (first name, last name, age) through an implicit intent.
- View the returned person, again through an implicit intent.
- Delete from the details screen, which returns the person to the host
  to drop.
- Survives rotation on both screens; the host's person also survives
  process recreation.
- No network, no permissions, no data collection.

## Folder structure

```text
implicit-intent-demo/
├── .github/workflows/android.yml     # CI: unit tests + debug build
├── app/
│   ├── build.gradle.kts             # App module configuration
│   ├── proguard-rules.pro           # Release shrinker rules
│   └── src/
│       ├── androidTest/             # Instrumentation + Compose UI tests
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/enonymous1/implicitintentdemo/
│       │   │   ├── MainActivity.kt      # Launcher host, edge-to-edge
│       │   │   ├── PersonActivity.kt    # Implicit-intent callee
│       │   │   ├── HostScreen.kt        # Material 3 host UI
│       │   │   ├── PersonScreen.kt      # Material 3 form/details UI
│       │   │   ├── HostViewModel.kt     # Host state, SavedStateHandle
│       │   │   ├── PersonContract.kt    # Person, validation, contract
│       │   │   └── PersonIntent.kt      # Intent/Bundle glue
│       │   └── res/                   # Strings, themes, icons, backup rules
│       └── test/                      # Local JVM unit tests
├── gradle/
│   ├── libs.versions.toml           # Version catalog
│   └── wrapper/                     # Gradle Wrapper configuration
├── build.gradle.kts                 # Root plugin configuration
├── gradle.properties                # AndroidX and Gradle settings
├── gradlew / gradlew.bat            # Gradle Wrapper scripts
└── settings.gradle.kts              # Project and module registration
```

## Requirements

- Android Studio with Android SDK 36 installed
- JDK 17 (Android Studio's bundled JDK works)
- An Android emulator or physical device running API 24 or newer

## Setup and run

1. Clone the repository and open the project directory in Android Studio.
2. Let Android Studio sync the Gradle project and install any requested
   SDK components.
3. Select an emulator or connected Android device.
4. Run the `app` configuration from Android Studio.

From a terminal at the project root, the debug build can also be created with:

```powershell
.\gradlew.bat assembleDebug
```

The generated APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
For a release build, use `assembleRelease` (R8 shrinking is enabled) or
`bundleRelease` for a Play Store bundle.

## Testing

Run local unit tests with:

```powershell
.\gradlew.bat testDebugUnitTest
```

Run connected-device instrumentation tests with:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Unit tests cover form validation (empty, non-numeric, negative,
overflowing, and out-of-range ages), the intent-contract actions, and the
host ViewModel (save, delete, and restore from saved state). The UI tests
are deterministic Compose checks for the host and person screens and do
not launch activities. The full intent round trip — host to person
screen and back, including rotation mid-form, backing out, and a direct
launch of the person screen with no action — still needs a manual run on
a device or emulator, as noted in the changelog.

## Non-goals

There is no list, search, edit-in-place, or persistence: the original
kept no data, and this demo keeps none either. The legacy Full Sail
action and extra names are supported only as an inbound compatibility
path for the CE08 host; new callers should use the current contract
names in `PersonContract.kt`.

## License

MIT — see [LICENSE](LICENSE).
