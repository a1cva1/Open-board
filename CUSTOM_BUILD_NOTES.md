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

## v2 changes (2026-11-02)

- **Keyboard font: Nothing dots → Inter.** The `keyboard_font` setting now
  defaults to "normal", which renders keys and suggestions in the bundled
  Inter font (`res/font/inter_family.xml`) instead of the NDot dot-matrix
  font. The Orbit theme no longer forces the dot font over the setting.
  Set `keyboard_font` back to "nothing" in code to restore the dots.
- **Text editing promoted.** The built-in Text Edit action (cursor arrows,
  select, cut/copy/paste, undo/redo — `TextEditAction`) was already in the
  code and registered; it is now first in the default Favorites list so it
  leads the expanded action bar.

## v3 changes (2026-11-02)

- **Rounder keys, Gboard-style popup, pill spacebar (Orbit theme).**
  Key corner radius 8dp → 14dp. Key preview bubble enlarged
  (48x24dp → 56x36dp) with a full pill radius (18dp). The spacebar is
  now drawn as a pill instead of a hard-edged slab; the theme's
  drawable cache now keys on (color, radius) so the pill shape does
  not leak onto letter keys that share the spacebar's color.
- **Translate box is properly editable.** Root cause of the
  "typewriter" behaviour: the box is a real EditText, but its cursor
  movements were never forwarded to the IME, so the IME kept its
  working cursor glued to the end of the text — taps mid-text did
  nothing and mistakes meant delete-and-retype. `ActionEditText` now
  reports selection changes through the action manager into
  `LatinIME.onUpdateSelection`, syncing the IME cursor with the box.
  The translate box is also multi-line now (wraps up to 5 lines)
  instead of one horizontally-scrolling line.
- Note: a Resize Keyboard screen already exists in the app
  (Settings → Typing → Resize Keyboard): height 33%–175% + bottom
  offset sliders. No change needed there.

## v4 changes (2026-11-02) — voice typing returns, on the Translate pattern

- **System voice typing restored (explicit-tap only).** At the owner's
  request, a "Voice Input (System)" action is back — but NOT the old
  Whisper system: no voice module, no model downloads, no in-app
  recording. The action fires LatinIME's legacy shortcut path, which
  switches the input method to the device's own voice IME (e.g.
  Google Voice Typing); that IME does any listening under its own
  permission and switches back when done. This app still holds NO
  RECORD_AUDIO permission and no audio ever passes through it —
  the same explicit-use pattern as Cloud Translate.
  Files: actions/SystemVoiceAction.kt (new), triggerSystemVoiceInput
  restored on the action manager, action registered in
  AllActionsMap and added to default Favorites.

## v5 changes (2026-11-02) — rebrand to LEAF BOARD

- **Name & identity.** The app is now "Leaf Board" (app label, IME name,
  settings, credits, README) with the owner's LB logo as the launcher
  icon. applicationId changed to com.leafboard.keyboard, so it installs
  as its own app. The upstream Apache-2.0 LICENSE.md and NOTICE stay in
  place, and the Credits screen keeps the AOSP / FUTO / Ridmik / font
  attributions — the licence requires those; everything that was the
  original developer's personal branding (name, photo card, email,
  social links) is removed, including the whole "About Developer"
  section on the home screen.
- **Leaf theme (new default look).** The default theme is now a
  three-colour light theme: white background, white letter keys with a
  fine warm outline, black letters, light-brown function keys, brown
  hints, and a brown pill spacebar. The Bauhaus multi-colour palette is
  retired (the old per-key colour function is kept in the source for
  reference but no longer used).
- Everything from v2–v4 is unchanged: Inter font, Gboard-style rounded
  keys / pill popup, editable Translate box, Text Edit first in
  favorites, system voice typing, and all removals (no RECORD_AUDIO,
  no screenshot code, no analytics).

## v6 changes (2026-11-02) — field fixes + GBoard theme family (lb3.zip)

- **Nothing fonts fully purged.** NDot is deleted (font + family files,
  ~11 MB) and every remaining reference now resolves to Inter: the
  suggestion-strip styles (which were still hard-coded to NDot), the
  "nothing" font branch, and the app Typography (titles/labels used the
  Doto dot font — now Inter). No dot-matrix type anywhere.
- **Dot-style icons replaced.** The seven nth_* vectors (delete, enter,
  shift, shifted, search, smile/emoji, translate) were drawn as fields
  of dots; they are now clean line/solid icons. Same resource names, so
  every key and action that used them picks up the new look.
- **Spacebar** is light brown like the other function keys (was solid
  brown); the key-press preview bubble returns to a contained
  44x30 dp pill so it no longer spills past the keys.
- **App screens de-Orbited.** Setup wizard now says Leaf Board (it
  literally welcomed users to "Nothing Keyboard"); the accessibility
  announcement matches. Home screen's hard-coded near-black palette is
  now the Leaf light palette, the settings back button matches, and
  the "Try Typing" capsule at the bottom of Home is removed.
- **Action bar:** the menu (More) button is pinned OUTSIDE the
  scrolling actions row — it no longer slides away when you swipe the
  panel.
- **New actions:** Resize Keyboard (popup with height + bottom-offset
  sliders) and One-Handed Mode (tap cycles off -> docked right ->
  docked left; the keyboard shrinks to 78% width and the touch region
  follows it, so the empty side falls through to the app). Both are in
  default Favorites and in the actions menu.
- **GBoard theme family (new presets, nothing removed):** GBoard
  Default (the light grey-blue board from the owner's screenshot:
  white keys, grey function keys, blue enter key), GBoard Dark,
  GBoard Blue, GBoard Green, GBoard Yellow, GBoard Pink. On these
  themes the spacebar label and outline use plain grey instead of the
  stock yellow accent.
- Floating keyboard mode was NOT added: this codebase has no floating
  support at all (verified in source); it is a separate build job, not
  a fix. All existing themes and features are untouched.

## v7 changes (2026-11-02) — settings unification, Gboard popup, floating mode (lb4.zip)

- **Settings are one Leaf interface now.** The custom home screen finally
  matches the brand: white background (was hard-coded black), header reads
  LEAF BOARD in Inter (was "ORBIT BOARD" in the dot font), tagline is
  "Private & offline by design" (was "Crafted for Nothing OS"), and the
  Bengali/rows toggle titles are dark text (they were white — invisible on
  the light cards since v6 lightened them). Section headers use Inter with
  a Leaf-brown dot (was the dot font + Nothing red). Page/section titles
  (ScreenTitle) are dark (were white). Navigation row icon chips are light
  brown with dark/brown icons (were black chips, white icons, red accents).
- **Settings leftovers removed/renamed.** The "Nothing Dot Font" option is
  gone from Keyboard Font Style (the font itself was deleted in v6; only
  Normal Font remains). Spacebar Color options renamed to the Leaf palette
  (Brown / Light Brown / Tan / White / Dark Brown) and the underlying
  paints remapped to browns — no red/blue/yellow spacebar is possible.
- **Key press popup is Gboard-style now.** Root cause of the giant black
  pill: the preview height config was 122dp and the feedback drawable had
  50dp of bottom padding with a dark colour. Height is now 46dp, the
  drawable is a 52x40 fully-rounded pill (white on light themes, surface
  grey on dark themes) with 10dp float padding, and the preview text
  colour follows the theme background instead of always being white.
  Key hint letters get 4dp padding (was 1dp) so they sit inside the key
  instead of on its border.
- **Translate box fixed + default target English.** The input-darkener
  overlay no longer installs its tap-catcher while an action window owns
  the input connection (it could swallow taps meant for the box), and the
  box now re-asserts its input connection and focus when tapped/attached.
  Default target language is English (was Bengali "bn"); the last-used
  language is still remembered.
- **One-handed mode, Gboard-style.** The empty side now carries a control
  strip with expand (back to full width) and swap-side buttons, like
  Gboard, instead of dead space.
- **Floating mode added.** New Floating Mode action: the keyboard becomes
  a rounded panel at 84% width lifted off the bottom, with a drag handle
  on its top edge — drag to move it sideways and up/down; position is
  remembered. The touch region follows the panel. One-handed and
  floating turn each other off. (An IME panel floats inside its own
  window area; it cannot overlay other apps like a system window.)

## v8 changes (2026-11-02) — Gboard-style Text Edit panel (lb5.zip)

- **Text Edit panel rebuilt to the Gboard layout** from the owner's
  screenshot: big left/right cursor pads (hold to keep moving), a middle
  column with up / Select / down where Select is a toggle — while it's
  on, the pads extend the selection — a clipboard column with text
  buttons (Select all, Cut, Copy, Paste), and a bottom row with
  jump-to-start, jump-to-end and a stronger Delete key (hold to keep
  deleting). Same underlying mechanics as before (cursor moves, Ctrl
  key events for clipboard ops); the old Ctrl/Shift toggle row is gone
  because Select covers its job, and undo/redo still live in the action
  bar as their own actions.
