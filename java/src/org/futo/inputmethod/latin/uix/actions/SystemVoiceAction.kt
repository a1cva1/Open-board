package org.futo.inputmethod.latin.uix.actions

import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action

/**
 * Voice typing via the system's voice IME (e.g. Google Voice Typing).
 *
 * This app never touches the microphone itself: pressing the action asks
 * LatinIME to switch input methods to the device's voice IME, which does
 * the listening and recognition under its own permission. Recognition
 * therefore happens only on this explicit tap, exactly like Translate
 * only sends text on an explicit tap. No audio is recorded, stored or
 * sent by this app, and the app holds no RECORD_AUDIO permission.
 */
val SystemVoiceInputAction = Action(
    icon = R.drawable.mic_fill,
    name = R.string.system_voice_input_action_title,
    simplePressImpl = { manager, _ ->
        manager.triggerSystemVoiceInput()
    },
    persistentState = null,
    windowImpl = null
)
