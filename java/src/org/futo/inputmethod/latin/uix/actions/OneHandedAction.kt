package org.futo.inputmethod.latin.uix.actions

import kotlinx.coroutines.launch
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.FloatingModeSetting
import org.futo.inputmethod.latin.uix.OneHandedModeSetting
import org.futo.inputmethod.latin.uix.getSettingBlocking
import org.futo.inputmethod.latin.uix.setSetting

/**
 * One-handed mode: cycles off -> docked right -> docked left -> off.
 * The keyboard surface shrinks to 78% width and docks to one side;
 * touches on the empty side fall through to the app underneath.
 */
val OneHandedAction = Action(
    icon = R.drawable.one_handed,
    name = R.string.one_handed_action_title,
    simplePressImpl = { manager, _ ->
        val context = manager.getContext()
        val next = (context.getSettingBlocking(OneHandedModeSetting) + 1) % 3
        manager.getLifecycleScope().launch {
            context.setSetting(OneHandedModeSetting, next)
            if (next != 0) {
                context.setSetting(FloatingModeSetting, false)
            }
        }
    },
    persistentState = null,
    windowImpl = null
)
