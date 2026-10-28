# Changelog

## 2.0.0 — 2026-10-28

Modernization of the original Full Sail CE07 implicit-intent exercise
(v1.0). The person screen does the same job — a form and a details view
that return a person as a result intent — and the app now runs standalone,
because a host screen that sends the intents is included.

This public repository is a squashed export of that modernization: the
code and tests below are the v2.0.0 tree, in a single commit, without
the earlier private history.

### Changed

- Rewrote in Kotlin with Jetpack Compose and Material 3; the XML layouts,
  AppCompat Activity, ConstraintLayout, and Toolbar menus are gone.
- Added a launcher host screen (`MainActivity` + `HostScreen`) that sends
  the add/view implicit intents and receives results through the Activity
  Result API, replacing the separate CE08 host the original depended on.
  The host keeps the returned person only in a `ViewModel` backed by
  `SavedStateHandle`, so it survives rotation and process death.
- Fixed the original's crashes: launching the person screen with no
  intent action no longer throws a null-pointer exception, an overflowing
  or non-numeric age no longer throws from `parseInt`, and a view intent
  with missing extras no longer reads a silent default age of zero.
  Unrecognised launches show an unsupported state with a Close button.
- Replaced the single Toast with one clear inline validation message per
  failure. Names are trimmed, and the age must be a whole number from
  0 to 150 (the original had no range).
- Moved the action and extra names into `PersonContract`, with the
  original Full Sail names kept as a legacy inbound path only. Results
  always carry the current names.
- Renamed the application ID and package from
  `com.example.lyonsjustin_ce07` to `com.enonymous1.implicitintentdemo`,
  and the app name to Implicit Intent Demo.
- Toolchain: AGP 8.13.0, Gradle 8.14.3, Kotlin 2.2.0, Java 17,
  compile/target SDK 36, minSdk 24, Kotlin DSL build scripts, and a version
  catalog (`gradle/libs.versions.toml`).
- Release builds now use R8 shrinking (`minifyEnabled`, `shrinkResources`).
- Removed the assignment TODO comments, the tracked `.idea` directory
  (now ignored), and the tracked 4.3 MB release APK.

### Added

- Real tests: form validation and contract actions, the host ViewModel
  (save, delete, restore from saved state), a package-name instrumentation
  test, and deterministic Compose UI tests for both screens.
- GitHub Actions CI: unit tests plus a debug build on every push and pull
  request to `main`.
- MIT LICENSE, this changelog, and a `.gitattributes` file normalizing
  line endings to LF.

### Notes and non-goals

- No persistence was added: neither screen stores a person, as in v1.0.
  Closing and reopening the app starts with no person.
- The full intent round trip was not verified on this machine (no JDK or
  Android SDK here): the build gate is CI plus Android Studio on Windows.
  On first run, check add → save → view → delete, rotation mid-form,
  backing out of the form with no result, and a direct launch of the
  person screen with no action (unsupported state, Close returns).
- The legacy contract names are the only trace of the original Full Sail
  namespace, kept deliberately for CE08 compatibility; the old package
  name appears only in this changelog line.

## 1.0 — 2025-05-14

Original release: Java 8, AGP 8.0.2, SDK 33, Views, a single activity
answering implicit intents from a separate host app.
