package org.futo.inputmethod.latin.uix.actions

import kotlinx.coroutines.launch
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.FloatingModeSetting
import org.futo.inputmethod.latin.uix.OneHandedModeSetting
import org.futo.inputmethod.latin.uix.getSettingBlocking
import org.futo.inputmethod.latin.uix.setSetting

/**
 * Floating mode: undocks the keyboard into a smaller panel that floats
 * above the bottom of the screen. Drag the handle on its top edge to
 * move it left/right and up/down. Turning it on turns one-handed off.
 */
val FloatingAction = Action(
    icon = R.drawable.floating,
    name = R.string.floating_action_title,
    simplePressImpl = { manager, _ ->
        val context = manager.getContext()
        val next = !context.getSettingBlocking(FloatingModeSetting)
        manager.getLifecycleScope().launch {
            context.setSetting(FloatingModeSetting, next)
            if (next) {
                context.setSetting(OneHandedModeSetting, 0)
            }
        }
    },
    persistentState = null,
    windowImpl = null
)
