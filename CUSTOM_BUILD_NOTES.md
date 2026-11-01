# Orbit Board — Custom Build Notes

This tree is a modified copy of Orbit Board (FUTO Keyboard codebase) with every
phone-home and voice feature cut out at the source level. It ships with NO
workflow file on purpose (the Actions robot may not push workflow files):
build it with the bootstrap workflow in the repo, or add your own
`.github/workflows/build.yml` (GitHub Actions → `assembleStableRelease`).

## Removed completely

- **Voice typing** — the `voiceinput-shared` module (Whisper engine, TensorFlow
  Lite / VAD / pocketfft AARs), the voice input action, the model downloader,
  the voice settings screen, the setup mic step, and the `RECORD_AUDIO`
  permission. Voice models can no longer be imported (`FileKind` no longer
  recognises them).
- **Update checker** — background job, settings "Check for Updates" section,
  update sheets, the in-keyboard update notice, and the dev-settings toggles.
- **Announcements** — announcement manager, sheets, and the settings entry.
- **Crash reporting (ACRA)** — both flavor `CrashLoggingApplication` classes,
  the ACRA libraries, and the Gradle crash-report config. The manifest no
  longer names a custom Application class.
- **Screenshot AI assistant (hidden in the original source)** — a capture
  service that screenshotted the current app, sent the image to
  `web.ndpnt.co`, and auto-typed the AI's reply. Removed entirely: the capture
  package, the media-projection permission, the spacebar-row AI key (layout
  XML + key style + key code -16), both legacy call sites, and the OkHttp
  library that only it used.
- **Unused network plumbing** — the legacy `latin/network` HTTP classes
  (no remaining callers) and their unit tests.

## Kept, on purpose

- **Cloud Translate** — the only network call left in the app. It sends the
  text you type into the Translate box to `translate.googleapis.com` **only
  when you tap the Translate button**. Because of it, the APK keeps the
  `INTERNET` permission.
- All offline features: typing, Bengali layouts (Avro / Probhat / Jatiyo /
  Akkhor), suggestions and autocorrect from bundled dictionaries, glide
  typing, clipboard history, action bar, emoji, themes, keyboard resizer,
  one-handed and split modes, haptics.
- Credits / Help / "explore downloads" links still open pages in your
  **browser** when tapped — the app itself sends nothing.
- Legacy AOSP voice-key plumbing remains in the core engine but is hard-wired
  off (`mShouldShowVoiceInputKey` / `mShowsVoiceInputKey` are always false),
  the same state it shipped in. One theme preset is still named
  "VoiceInputTheme" — it is only a dark colour scheme, not a voice feature.

## Permissions after this build

`INTERNET` (Translate only), notifications, vibrate, boot-completed,
wake-lock, foreground service, user dictionary read/write, Nothing Glyph.
**No** `RECORD_AUDIO`, **no** media-projection.
