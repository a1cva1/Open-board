package org.futo.inputmethod.latin.uix.actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.Action
import org.futo.inputmethod.latin.uix.ActionWindow
import org.futo.inputmethod.latin.uix.KeyboardBottomOffsetSetting
import org.futo.inputmethod.latin.uix.KeyboardHeightMultiplierSetting
import org.futo.inputmethod.latin.uix.KeyboardManagerForAction
import org.futo.inputmethod.latin.uix.getSettingFlow
import org.futo.inputmethod.latin.uix.setSetting

private class ResizeActionWindow(val manager: KeyboardManagerForAction) : ActionWindow {
    @Composable
    override fun windowName(): String {
        return "Resize Keyboard"
    }

    @Composable
    override fun WindowContents(keyboardShown: Boolean) {
        val context = manager.getContext()
        val scope = manager.getLifecycleScope()

        val height by context.getSettingFlow(KeyboardHeightMultiplierSetting)
            .collectAsState(initial = KeyboardHeightMultiplierSetting.default)
        val offset by context.getSettingFlow(KeyboardBottomOffsetSetting)
            .collectAsState(initial = KeyboardBottomOffsetSetting.default)

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Keyboard height: ${(height * 100).toInt()}%")
            Slider(
                value = height,
                onValueChange = {
                    scope.launch { context.setSetting(KeyboardHeightMultiplierSetting, it) }
                },
                valueRange = 0.33f..1.75f
            )

            Text("Bottom offset: ${offset.toInt()} dp")
            Slider(
                value = offset,
                onValueChange = {
                    scope.launch { context.setSetting(KeyboardBottomOffsetSetting, it) }
                },
                valueRange = 0f..50f
            )
        }
    }
}

val ResizeKeyboardAction = Action(
    icon = R.drawable.maximize,
    name = R.string.resize_keyboard_action_title,
    canShowKeyboard = true,
    windowImpl = { manager, _ -> ResizeActionWindow(manager) },
    simplePressImpl = null
)
