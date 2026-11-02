package org.futo.inputmethod.latin.uix

import android.app.Activity
import android.app.KeyguardManager
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InlineSuggestionsResponse
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputContentInfo
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.BiasAlignment
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.material3.Icon
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.futo.inputmethod.accessibility.AccessibilityUtils
import org.futo.inputmethod.latin.AudioAndHapticFeedbackManager
import org.futo.inputmethod.latin.BuildConfig
import org.futo.inputmethod.latin.LanguageSwitcherDialog
import org.futo.inputmethod.latin.LatinIME
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.SuggestedWords
import org.futo.inputmethod.latin.SuggestedWords.SuggestedWordInfo
import org.futo.inputmethod.latin.common.Constants
import org.futo.inputmethod.latin.inputlogic.InputLogic
import org.futo.inputmethod.latin.suggestions.SuggestionStripView
import org.futo.inputmethod.latin.uix.actions.ActionEditor
import org.futo.inputmethod.latin.uix.actions.ActionRegistry
import org.futo.inputmethod.latin.uix.actions.AllActions
import org.futo.inputmethod.latin.uix.actions.EmojiAction
import org.futo.inputmethod.latin.uix.settings.DataStoreCacheProvider
import org.futo.inputmethod.latin.uix.settings.SettingsActivity
import org.futo.inputmethod.latin.uix.settings.pages.ActionBarDisplayedSetting
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.theme.ThemeOption
import org.futo.inputmethod.latin.uix.theme.Typography
import org.futo.inputmethod.latin.uix.theme.UixThemeAuto
import org.futo.inputmethod.latin.uix.theme.UixThemeWrapper
import java.util.Locale

val LocalManager = staticCompositionLocalOf<KeyboardManagerForAction> {
    error("No LocalManager provided")
}

val LocalThemeProvider = staticCompositionLocalOf<DynamicThemeProvider> {
    error("No LocalThemeProvider provided")
}

private class LatinIMEActionInputTransaction(
    private val inputLogic: InputLogic,
    shouldApplySpace: Boolean,
    private val context: Context
): ActionInputTransaction {
    private val isSpaceNecessary: Boolean
    private var isFinished = false

    init {
        val priorText = inputLogic.mConnection.getTextBeforeCursor(1, 0)
        isSpaceNecessary = shouldApplySpace && !priorText.isNullOrEmpty() && !priorText.last().isWhitespace()

        inputLogic.startSuppressingLogic()
    }

    private fun transformText(text: String): String {
        return if(isSpaceNecessary) { " $text" } else { text }
    }

    private var previousText = ""
    override fun updatePartial(text: String) {
        if(isFinished) return
        previousText = text
        inputLogic.mConnection.setComposingText(
            transformText(text),
            1
        )
    }

    override fun commit(text: String) {
        if(isFinished) return
        isFinished = true
        inputLogic.mConnection.commitText(
            transformText(text),
            1
        )
        inputLogic.endSuppressingLogic()
    }

    override fun cancel() {
        commit(previousText)
    }
}

class UixActionKeyboardManager(val uixManager: UixManager, val latinIME: LatinIME) : KeyboardManagerForAction {
    override fun getContext(): Context {
        return latinIME
    }

    override fun getLifecycleScope(): LifecycleCoroutineScope {
        return latinIME.lifecycleScope
    }

    override fun triggerContentUpdate() {
        uixManager.setContent()
    }

    override fun createInputTransaction(applySpaceIfNeeded: Boolean): ActionInputTransaction {
        return LatinIMEActionInputTransaction(latinIME.inputLogic, applySpaceIfNeeded, latinIME)
    }

    override fun typeText(v: String) {
        if(latinIME.isInputConnectionOverridden) {
            latinIME.getBaseInputConnection()?.commitText(v, 1)
        } else {
            latinIME.latinIMELegacy.onTextInput(v)
        }
    }

    override fun replaceAllText(v: String) {
        val ic = latinIME.getBaseInputConnection()
        if (ic != null) {
            ic.setComposingRegion(0, ic.getExtractedText(ExtractedTextRequest(), 0)?.text?.length ?: 0)
            ic.setComposingText("", 1)
            ic.commitText(v, 1)
        }
    }

    override fun typeUri(uri: Uri, mimeTypes: List<String>): Boolean {
        if(mimeTypes.isEmpty()) {
            Log.w("UixManager", "mimeTypes is empty")
            return false
        }

        val description = ClipDescription("Pasted image", mimeTypes.toTypedArray())

        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            InputContentInfo(uri, description, null)
        } else {
            return false
        }

        return latinIME.currentInputConnection?.commitContent(info, InputConnection.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, null) ?: run {
            Log.w("UixManager", "Current input connection is null")
            return false
        }
    }

    override fun backspace(amount: Int) {
        latinIME.latinIMELegacy.onCodeInput(
            Constants.CODE_DELETE,
            Constants.NOT_A_COORDINATE,
            Constants.NOT_A_COORDINATE, false)
    }

    override fun closeActionWindow() {
        if(uixManager.currWindowActionWindow == null) return
        uixManager.returnBackToMainKeyboardViewFromAction()
    }

    override fun updateTheme(newTheme: ThemeOption) {
        latinIME.updateTheme(newTheme)
    }

    override fun getThemeProvider(): DynamicThemeProvider {
        return latinIME.getDrawableProvider()
    }

    override fun sendCodePointEvent(codePoint: Int) {
        latinIME.latinIMELegacy.onCodeInput(codePoint,
            Constants.NOT_A_COORDINATE,
            Constants.NOT_A_COORDINATE, false)
    }

    override fun sendKeyEvent(keyCode: Int, metaState: Int) {
        latinIME.inputLogic.sendDownUpKeyEvent(keyCode, metaState)
    }

    override fun cursorLeft(steps: Int, stepOverWords: Boolean, select: Boolean) {
        latinIME.inputLogic.cursorLeft(steps, stepOverWords, select)
    }

    override fun cursorRight(steps: Int, stepOverWords: Boolean, select: Boolean) {
        latinIME.inputLogic.cursorRight(steps, stepOverWords, select)
    }

    override fun performHapticAndAudioFeedback(code: Int, view: View) {
        AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(code, view)
    }

    override fun getActiveLocale(): Locale {
        return latinIME.latinIMELegacy.locale
    }

    override fun overrideInputConnection(inputConnection: InputConnection, editorInfo: EditorInfo) {
        latinIME.overrideInputConnection(inputConnection, editorInfo)
        uixManager.toggleExpandAction(true)
        uixManager.isInputOverridden.value = true
    }

    override fun unsetInputConnection() {
        latinIME.overrideInputConnection(null, null)
        uixManager.isInputOverridden.value = false
    }

    override fun triggerSystemVoiceInput() {
        latinIME.latinIMELegacy.onCodeInput(
            Constants.CODE_SHORTCUT,
            Constants.SUGGESTION_STRIP_COORDINATE,
            Constants.SUGGESTION_STRIP_COORDINATE,
            false
        )
    }

    override fun notifyEditorSelectionChanged(selStart: Int, selEnd: Int) {
        // Keep the IME's internal cursor in sync with the in-keyboard editor
        // box (e.g. Translate), so tapping mid-text moves the working cursor
        // there instead of leaving it glued to the end (typewriter effect).
        if (latinIME.isInputConnectionOverridden) {
            latinIME.onUpdateSelection(selStart, selEnd, selStart, selEnd, -1, -1)
        }
    }

    override fun requestDialog(text: String, options: List<DialogRequestItem>, onCancel: () -> Unit) {
        uixManager.activeDialogRequest.value = ActiveDialogRequest(text, options, onCancel)
        uixManager.activeDialogRequestDismissed.value = false
    }

    override fun openInputMethodPicker() {
        uixManager.showLanguageSwitcher()
    }

    override fun announce(s: String) {
        AccessibilityUtils.init(getContext())
        if(AccessibilityUtils.getInstance().isAccessibilityEnabled) {
            AccessibilityUtils.getInstance().announceForAccessibility(uixManager.getComposeView(), s)
        }
    }

    override fun activateAction(action: Action) {
        uixManager.onActionActivated(action)
    }

    override fun showActionEditor() {
        uixManager.showActionEditor()
    }

    override fun isDeviceLocked(): Boolean {
        return getContext().isDeviceLocked
    }

    override fun getLatinIMEForDebug(): LatinIME = latinIME
}

data class ActiveDialogRequest(
    val text: String,
    val options: List<DialogRequestItem>,
    val onCancel: () -> Unit
)

class UixManager(private val latinIME: LatinIME) {
    private var shouldShowSuggestionStrip: Boolean = true
    private var suggestedWords: SuggestedWords? = null

    private var composeView: ComposeView? = null

    private var currWindowAction: Action? = null
    private var persistentStates: HashMap<Action, PersistentActionState?> = hashMapOf()

    private var inlineSuggestions: List<MutableState<View?>> = listOf()
    private val keyboardManagerForAction = UixActionKeyboardManager(this, latinIME)

    private var mainKeyboardHidden = false

    private var numSuggestionsSinceNotice = 0
    private var currentNotice: MutableState<ImportantNotice?> = mutableStateOf(null)

    private var isActionsExpanded = mutableStateOf(false)
    private fun toggleActionsExpanded() {
        isActionsExpanded.value = !isActionsExpanded.value
        latinIME.deferSetSetting(ActionBarExpanded, isActionsExpanded.value)
    }

    private var isShowingActionEditor = mutableStateOf(false)
    fun showActionEditor() {
        isShowingActionEditor.value = true
    }

    var isInputOverridden = mutableStateOf(false)

    var currWindowActionWindow: ActionWindow? = null

    val isMainKeyboardHidden get() = mainKeyboardHidden

    fun onActionActivated(rawAction: Action) {
        latinIME.inputLogic.finishInput()

        val action = runBlocking {
            ActionRegistry.getActionOverride(latinIME, rawAction)
        }

        if (action.windowImpl != null) {
            enterActionWindowView(action)
        } else if (action.simplePressImpl != null) {
            action.simplePressImpl.invoke(keyboardManagerForAction, persistentStates[action])
        } else {
            throw IllegalStateException("An action must have either a window implementation or a simple press implementation")
        }
    }

    fun onActionAltActivated(rawAction: Action) {
        latinIME.inputLogic.finishInput()

        val action = runBlocking {
            ActionRegistry.getActionOverride(latinIME, rawAction)
        }

        action.altPressImpl?.invoke(keyboardManagerForAction, persistentStates[action])
    }

    @Composable
    private fun MainKeyboardViewWithActionBar() {
        val view = LocalView.current

        val actionBarShown = useDataStore(ActionBarDisplayedSetting)

        Column {
            // Don't show suggested words when it's not meant to be shown
            val suggestedWordsOrNull = if(shouldShowSuggestionStrip) {
                suggestedWords
            } else {
                null
            }

            if(actionBarShown.value) {
                ActionBar(
                    suggestedWordsOrNull,
                    latinIME.latinIMELegacy as SuggestionStripView.Listener,
                    inlineSuggestions = inlineSuggestions,
                    onActionActivated = {
                        keyboardManagerForAction.performHapticAndAudioFeedback(
                            Constants.CODE_TAB,
                            view
                        )
                        onActionActivated(it)
                    },
                    onActionAltActivated = {
                        if (it.altPressImpl != null) {
                            keyboardManagerForAction.performHapticAndAudioFeedback(
                                Constants.CODE_TAB,
                                view
                            )
                        }
                        onActionAltActivated(it)
                    },
                    importantNotice = currentNotice.value,
                    keyboardManagerForAction = keyboardManagerForAction,
                    isActionsExpanded = isActionsExpanded.value,
                    toggleActionsExpanded = { toggleActionsExpanded() },
                )
            }
        }
    }

    private fun enterActionWindowView(action: Action) {
        assert(action.windowImpl != null)

        mainKeyboardHidden = true

        currWindowAction = action

        if (persistentStates[action] == null) {
            persistentStates[action] = action.persistentState?.let { it(keyboardManagerForAction) }
        }

        currWindowActionWindow = (action.windowImpl!!)(keyboardManagerForAction, persistentStates[action])

        if(action.keepScreenAwake) {
            latinIME.window.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setContent()

        keyboardManagerForAction.announce("${latinIME.resources.getString(action.name)} mode")
    }

    fun returnBackToMainKeyboardViewFromAction() {
        if(currWindowActionWindow == null) return

        val name = latinIME.resources.getString(currWindowAction!!.name)

        currWindowActionWindow!!.close()

        currWindowAction = null
        currWindowActionWindow = null

        mainKeyboardHidden = false

        latinIME.onKeyboardShown()

        latinIME.window.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent()

        keyboardManagerForAction.announce("$name closed")
    }

    fun toggleExpandAction(to: Boolean? = null) {
        mainKeyboardHidden = !(to ?: mainKeyboardHidden)
        if(!mainKeyboardHidden) {
            latinIME.onKeyboardShown()
        }

        setContent()
    }

    @Composable
    private fun ActionViewWithHeader(windowImpl: ActionWindow) {
        val heightDiv = if(mainKeyboardHidden) {
            1
        } else {
            1.5
        }
        Column {
            if(mainKeyboardHidden || latinIME.isInputConnectionOverridden) {
                ActionWindowBar(
                    onBack = { returnBackToMainKeyboardViewFromAction() },
                    canExpand = currWindowAction!!.canShowKeyboard,
                    onExpand = { toggleExpandAction() },
                    windowTitleBar = { windowImpl.WindowTitleBar(this) }
                )
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) {
                    (latinIME
                        .getInputViewHeight()
                        .toFloat() / heightDiv.toFloat()).toDp()
                })
            ) {
                windowImpl.WindowContents(keyboardShown = !isMainKeyboardHidden)
            }

            if(!mainKeyboardHidden && !latinIME.isInputConnectionOverridden) {
                val suggestedWordsOrNull = if (shouldShowSuggestionStrip) {
                    suggestedWords
                } else {
                    null
                }

                CollapsibleSuggestionsBar(
                    onCollapse = { toggleExpandAction() },
                    onClose = { returnBackToMainKeyboardViewFromAction() },
                    words = suggestedWordsOrNull,
                    suggestionStripListener = latinIME.latinIMELegacy as SuggestionStripView.Listener
                )
            }
        }
    }

    val activeDialogRequest: MutableState<ActiveDialogRequest?> = mutableStateOf(null)
    val activeDialogRequestDismissed: MutableState<Boolean> = mutableStateOf(true)

    @Composable
    fun BoxScope.ForgetWordDialog() {
        AnimatedVisibility(
            visible = !activeDialogRequestDismissed.value,
            modifier = Modifier.matchParentSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (activeDialogRequest.value != null) {
                Box(modifier = Modifier.matchParentSize()) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.66f),
                        modifier = Modifier
                            .matchParentSize()
                            .pointerInput(Unit) {
                                this.detectTapGestures(onPress = {
                                    activeDialogRequestDismissed.value = true
                                    activeDialogRequest.value?.onCancel?.invoke()
                                })
                            }
                    ) { }

                    Box(modifier = Modifier
                        .matchParentSize()
                        .padding(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    activeDialogRequest.value?.text ?: "",
                                    style = Typography.bodyMedium
                                )

                                Row {
                                    activeDialogRequest.value?.options?.forEach {
                                        TextButton(
                                            onClick = {
                                                it.onClick()
                                                activeDialogRequestDismissed.value = true
                                            }
                                        ) {
                                            Text(it.option)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private var languageSwitcherDialog: DialogComposeView? = null
    fun showLanguageSwitcher() {
        // Dismiss old dialog
        languageSwitcherDialog?.dismiss()

        // Create new dialog
        languageSwitcherDialog = createDialogComposeView(latinIME) {
            DataStoreCacheProvider {
                UixThemeAuto {
                    LanguageSwitcherDialog(
                        onDismiss = { it.dismiss() }
                    )
                }
            }
        }

        languageSwitcherDialog?.show()
    }

    @Composable
    fun ActionEditorHost() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            AnimatedVisibility(
                visible = isShowingActionEditor.value,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                content = {
                    ActionEditor()
                },
            )
        }
    }

    @Composable
    fun InputDarkener(darken: Boolean, captureTaps: Boolean, onClose: () -> Unit) {
        val color by animateColorAsState(
            if (darken) Color.Black.copy(alpha = 0.25f) else Color.Transparent
        )

        LaunchedEffect(darken) {
            latinIME.setInputModal(darken)
        }

        Box(Modifier
            .background(color)
            .fillMaxWidth()
            .fillMaxHeight()
            .then(
                if (captureTaps) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures {
                            onClose()
                        }
                    }
                } else {
                    Modifier
                })
        )
    }

    fun setContent() {
        composeView?.setContent {
            UixThemeWrapper(latinIME.colorScheme) {
                DataStoreCacheProvider {
                    CompositionLocalProvider(LocalManager provides keyboardManagerForAction) {
                        CompositionLocalProvider(LocalThemeProvider provides latinIME.getDrawableProvider()) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                InputDarkener(
                                    darken = isInputOverridden.value || isShowingActionEditor.value,
                                    captureTaps = isShowingActionEditor.value
                                ) {
                                    closeActionWindow()
                                    isShowingActionEditor.value = false
                                }

                                val oneHandedMode by latinIME.getSettingFlow(OneHandedModeSetting).collectAsState(initial = 0)

                                val floatingMode by latinIME.getSettingFlow(FloatingModeSetting).collectAsState(initial = false)
                                val floatXPref by latinIME.getSettingFlow(FloatingXSetting).collectAsState(initial = 0.5f)
                                val floatLiftPref by latinIME.getSettingFlow(FloatingLiftSetting).collectAsState(initial = 64f)
                                var floatDragX by remember { mutableStateOf<Float?>(null) }
                                var floatDragLift by remember { mutableStateOf<Float?>(null) }
                                val floatX = floatDragX ?: floatXPref
                                val floatLift = floatDragLift ?: floatLiftPref

                                @Composable
                                fun OneHandedStrip(mode: Int) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(1f - ONE_HANDED_FRACTION)
                                            .padding(top = 28.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(18.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(latinIME.colorScheme.surfaceVariant)
                                                .clickable {
                                                    keyboardManagerForAction.getLifecycleScope().launch {
                                                        keyboardManagerForAction.getContext()
                                                            .setSetting(OneHandedModeSetting, 0)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.maximize),
                                                contentDescription = null,
                                                tint = latinIME.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(latinIME.colorScheme.surfaceVariant)
                                                .clickable {
                                                    keyboardManagerForAction.getLifecycleScope().launch {
                                                        keyboardManagerForAction.getContext()
                                                            .setSetting(OneHandedModeSetting, 3 - mode)
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.swap_horiz),
                                                contentDescription = null,
                                                tint = latinIME.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }

                                @Composable
                                fun KeyboardSurfaceContents() {
                                    Box {
                                        Column {
                                            when {
                                                currWindowActionWindow != null -> ActionViewWithHeader(
                                                    currWindowActionWindow!!
                                                )

                                                else -> MainKeyboardViewWithActionBar()
                                            }

                                            latinIME.LegacyKeyboardView(hidden = isMainKeyboardHidden)
                                        }

                                        ForgetWordDialog()

                                        if (floatingMode) {
                                            val gripDensity = LocalDensity.current
                                            val gripConfig = LocalConfiguration.current
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopCenter)
                                                    .offset(y = (-9).dp)
                                                    .size(width = 64.dp, height = 18.dp)
                                                    .clip(RoundedCornerShape(9.dp))
                                                    .background(latinIME.colorScheme.onBackground.copy(alpha = 0.30f))
                                                    .pointerInput(Unit) {
                                                        detectDragGestures(
                                                            onDragStart = { },
                                                            onDragEnd = {
                                                                val newX = floatDragX
                                                                val newLift = floatDragLift
                                                                keyboardManagerForAction.getLifecycleScope().launch {
                                                                    val ctx = keyboardManagerForAction.getContext()
                                                                    if (newX != null) ctx.setSetting(FloatingXSetting, newX)
                                                                    if (newLift != null) ctx.setSetting(FloatingLiftSetting, newLift)
                                                                }
                                                                floatDragX = null
                                                                floatDragLift = null
                                                            },
                                                            onDragCancel = {
                                                                floatDragX = null
                                                                floatDragLift = null
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                val slackPx = with(gripDensity) {
                                                                    (gripConfig.screenWidthDp.toFloat() * (1f - FLOATING_WIDTH_FRACTION)).dp.toPx()
                                                                }
                                                                if (slackPx > 0f) {
                                                                    floatDragX = ((floatDragX ?: floatXPref) + dragAmount.x / slackPx).coerceIn(0f, 1f)
                                                                }
                                                                floatDragLift = ((floatDragLift ?: floatLiftPref) - dragAmount.y / gripDensity.density).coerceIn(0f, 200f)
                                                            }
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Spacer(modifier = Modifier.weight(1.0f))

                                    val keyboardSurfaceModifier = Modifier
                                        .onSizeChanged {
                                            latinIME.updateTouchableHeight(it.height)
                                        }
                                        .then(
                                            if (floatingMode) {
                                                Modifier
                                                    .align(BiasAlignment.Horizontal(-1f + 2f * floatX))
                                                    .fillMaxWidth(FLOATING_WIDTH_FRACTION)
                                                    .padding(bottom = floatLift.dp)
                                            } else {
                                                Modifier
                                            }
                                        )

                                    if (!floatingMode && oneHandedMode != 0) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            if (oneHandedMode == 1) {
                                                OneHandedStrip(oneHandedMode)
                                            }

                                            Surface(
                                                modifier = keyboardSurfaceModifier.weight(1f),
                                                color = latinIME.keyboardColor
                                            ) {
                                                KeyboardSurfaceContents()
                                            }

                                            if (oneHandedMode == 2) {
                                                OneHandedStrip(oneHandedMode)
                                            }
                                        }
                                    } else {
                                        Surface(
                                            modifier = keyboardSurfaceModifier,
                                            color = latinIME.keyboardColor,
                                            shape = if (floatingMode) RoundedCornerShape(24.dp) else RectangleShape,
                                            shadowElevation = if (floatingMode) 12.dp else 0.dp
                                        ) {
                                            KeyboardSurfaceContents()
                                        }
                                    }
                                }

                                ActionEditorHost()
                            }
                        }
                    }
                }
            }
        }
    }

    fun createComposeView(): View {
        if(composeView != null) {
            composeView = null
            //throw IllegalStateException("Attempted to create compose view, when one is already created!")
        }

        composeView = ComposeView(latinIME).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setParentCompositionContext(null)

            latinIME.setOwners()
        }

        setContent()

        return composeView!!
    }

    fun getComposeView(): View? {
        return composeView
    }

    fun onColorSchemeChanged() {
        setContent()
    }

    fun onInputFinishing() {
        closeActionWindow()
        languageSwitcherDialog?.dismiss()
        isShowingActionEditor.value = false
    }

    fun cleanUpPersistentStates() {
        println("Cleaning up persistent states")
        for((key, value) in persistentStates.entries) {
            if(currWindowAction != key) {
                latinIME.lifecycleScope.launch { value?.cleanUp() }
            }
        }
    }

    fun closeActionWindow() {
        if(currWindowActionWindow == null) return
        returnBackToMainKeyboardViewFromAction()
    }


    fun updateVisibility(shouldShowSuggestionsStrip: Boolean, fullscreenMode: Boolean) {
        this.shouldShowSuggestionStrip = shouldShowSuggestionsStrip
        setContent()
    }

    fun setSuggestions(suggestedWords: SuggestedWords?, rtlSubtype: Boolean) {
        this.suggestedWords = suggestedWords
        setContent()

        if(currentNotice.value != null) {
            numSuggestionsSinceNotice += 1
            if(numSuggestionsSinceNotice > 4) {
                currentNotice.value?.onDismiss(latinIME)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun onInlineSuggestionsResponse(response: InlineSuggestionsResponse): Boolean {
        currentNotice.value?.onDismiss(latinIME)

        inlineSuggestions = response.inlineSuggestions.map {
            latinIME.inflateInlineSuggestion(it)
        }
        setContent()

        return true
    }

    fun openEmojiKeyboard() {
        if(currWindowAction == null) {
            onActionActivated(EmojiAction)
        }
    }

    fun triggerAction(id: Int, alt: Boolean) {
        val action = AllActions.getOrNull(id) ?: throw IllegalArgumentException("No such action with ID $id")

        if(alt) {
            onActionAltActivated(action)
        } else {
            if (currWindowAction != null && action.windowImpl != null) {
                closeActionWindow()
            }

            onActionActivated(action)
        }
    }

    fun requestForgetWord(suggestedWordInfo: SuggestedWords.SuggestedWordInfo) {
        keyboardManagerForAction.requestDialog(
            latinIME.getString(R.string.blacklist_from_suggestions, suggestedWordInfo.mWord),
            listOf(
                DialogRequestItem(latinIME.getString(R.string.cancel)) { },
                DialogRequestItem(latinIME.getString(R.string.blacklist)) {
                    latinIME.forceForgetWord(suggestedWordInfo)
                },
            ) + if(suggestedWordInfo.mKindAndFlags == SuggestedWordInfo.KIND_EMOJI_SUGGESTION) {
                listOf(
                    DialogRequestItem(latinIME.getString(R.string.disable_emoji)) {
                        runBlocking { latinIME.setSetting(SHOW_EMOJI_SUGGESTIONS, false) }
                        latinIME.refreshSuggestions()
                    }
                )
            } else {
                listOf()
            },
            { }
        )

        val v = latinIME.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v!!.vibrate(50)
        }
    }

    fun onCreate() {
        AllActions.forEach { action ->
            if(action.persistentStateInitialization == PersistentStateInitialization.OnKeyboardLoad) {
                persistentStates[action] = action.persistentState?.let { it(keyboardManagerForAction) }
            }
        }

        isActionsExpanded.value = latinIME.getSettingBlocking(ActionBarExpanded)
    }

    fun onPersistentStatesUnlocked() {
        persistentStates.forEach {
            latinIME.lifecycleScope.launch {
                it.value?.onDeviceUnlocked()
            }
        }
    }
}
                                            .pointerInput(Unit) {
                                                        detectDragGestures(
                                                            onDragEnd = {
                                                                val newX = floatDragX
                                                                val newLift = floatDragLift
                                                                keyboardManagerForAction.getLifecycleScope().launch {
                                                                    val ctx = keyboardManagerForAction.getContext()
                                                                    if (newX != null) ctx.setSetting(FloatingXSetting, newX)
                                                                    if (newLift != null) ctx.setSetting(FloatingLiftSetting, newLift)
                                                                }
                                                                floatDragX = null
                                                                floatDragLift = null
                                                            },
                                                            onDragCancel = {
                                                                floatDragX = null
                                                                floatDragLift = null
                                                            }
                                                        ) { change, dragAmount ->
                                                            change.consume()
                                                            val slackPx = with(gripDensity) {
                                                                (gripConfig.screenWidthDp.toFloat() * (1f - FLOATING_WIDTH_FRACTION)).dp.toPx()
                                                            }
                                                            if (slackPx > 0f) {
                                                                floatDragX = ((floatDragX ?: floatXPref) + dragAmount.x / slackPx).coerceIn(0f, 1f)
                                                            }
                                                            floatDragLift = ((floatDragLift ?: floatLiftPref) - dragAmount.y / gripDensity.density).coerceIn(0f, 200f)
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Spacer(modifier = Modifier.weight(1.0f))

                                    val keyboardSurfaceModifier = Modifier
                                        .onSizeChanged {
                                            latinIME.updateTouchableHeight(it.height)
                                        }
                                        .then(
                                            if (floatingMode) {
                                                Modifier
                                                    .align(BiasAlignment.Horizontal(-1f + 2f * floatX))
                                                    .fillMaxWidth(FLOATING_WIDTH_FRACTION)
                                                    .padding(bottom = floatLift.dp)
                                            } else {
                                                Modifier
                                            }
                                        )

                                    if (!floatingMode && oneHandedMode != 0) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            if (oneHandedMode == 1) {
                                                OneHandedStrip(oneHandedMode)
                                            }

                                            Surface(
                                                modifier = keyboardSurfaceModifier.weight(1f),
                                                color = latinIME.keyboardColor
                                            ) {
                                                KeyboardSurfaceContents()
                                            }

                                            if (oneHandedMode == 2) {
                                                OneHandedStrip(oneHandedMode)
                                            }
                                        }
                                    } else {
                                        Surface(
                                            modifier = keyboardSurfaceModifier,
                                            color = latinIME.keyboardColor,
                                            shape = if (floatingMode) RoundedCornerShape(24.dp) else RectangleShape,
                                            shadowElevation = if (floatingMode) 12.dp else 0.dp
                                        ) {
                                            KeyboardSurfaceContents()
                                        }
                                    }
                                }

                                ActionEditorHost()
                            }
                        }
                    }
                }
            }
        }
    }

    fun createComposeView(): View {
        if(composeView != null) {
            composeView = null
            //throw IllegalStateException("Attempted to create compose view, when one is already created!")
        }

        composeView = ComposeView(latinIME).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setParentCompositionContext(null)

            latinIME.setOwners()
        }

        setContent()

        return composeView!!
    }

    fun getComposeView(): View? {
        return composeView
    }

    fun onColorSchemeChanged() {
        setContent()
    }

    fun onInputFinishing() {
        closeActionWindow()
        languageSwitcherDialog?.dismiss()
        isShowingActionEditor.value = false
    }

    fun cleanUpPersistentStates() {
        println("Cleaning up persistent states")
        for((key, value) in persistentStates.entries) {
            if(currWindowAction != key) {
                latinIME.lifecycleScope.launch { value?.cleanUp() }
            }
        }
    }

    fun closeActionWindow() {
        if(currWindowActionWindow == null) return
        returnBackToMainKeyboardViewFromAction()
    }


    fun updateVisibility(shouldShowSuggestionsStrip: Boolean, fullscreenMode: Boolean) {
        this.shouldShowSuggestionStrip = shouldShowSuggestionsStrip
        setContent()
    }

    fun setSuggestions(suggestedWords: SuggestedWords?, rtlSubtype: Boolean) {
        this.suggestedWords = suggestedWords
        setContent()

        if(currentNotice.value != null) {
            numSuggestionsSinceNotice += 1
            if(numSuggestionsSinceNotice > 4) {
                currentNotice.value?.onDismiss(latinIME)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun onInlineSuggestionsResponse(response: InlineSuggestionsResponse): Boolean {
        currentNotice.value?.onDismiss(latinIME)

        inlineSuggestions = response.inlineSuggestions.map {
            latinIME.inflateInlineSuggestion(it)
        }
        setContent()

        return true
    }

    fun openEmojiKeyboard() {
        if(currWindowAction == null) {
            onActionActivated(EmojiAction)
        }
    }

    fun triggerAction(id: Int, alt: Boolean) {
        val action = AllActions.getOrNull(id) ?: throw IllegalArgumentException("No such action with ID $id")

        if(alt) {
            onActionAltActivated(action)
        } else {
            if (currWindowAction != null && action.windowImpl != null) {
                closeActionWindow()
            }

            onActionActivated(action)
        }
    }

    fun requestForgetWord(suggestedWordInfo: SuggestedWords.SuggestedWordInfo) {
        keyboardManagerForAction.requestDialog(
            latinIME.getString(R.string.blacklist_from_suggestions, suggestedWordInfo.mWord),
            listOf(
                DialogRequestItem(latinIME.getString(R.string.cancel)) { },
                DialogRequestItem(latinIME.getString(R.string.blacklist)) {
                    latinIME.forceForgetWord(suggestedWordInfo)
                },
            ) + if(suggestedWordInfo.mKindAndFlags == SuggestedWordInfo.KIND_EMOJI_SUGGESTION) {
                listOf(
                    DialogRequestItem(latinIME.getString(R.string.disable_emoji)) {
                        runBlocking { latinIME.setSetting(SHOW_EMOJI_SUGGESTIONS, false) }
                        latinIME.refreshSuggestions()
                    }
                )
            } else {
                listOf()
            },
            { }
        )

        val v = latinIME.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v!!.vibrate(50)
        }
    }

    fun onCreate() {
        AllActions.forEach { action ->
            if(action.persistentStateInitialization == PersistentStateInitialization.OnKeyboardLoad) {
                persistentStates[action] = action.persistentState?.let { it(keyboardManagerForAction) }
            }
        }

        isActionsExpanded.value = latinIME.getSettingBlocking(ActionBarExpanded)
    }

    fun onPersistentStatesUnlocked() {
        persistentStates.forEach {
            latinIME.lifecycleScope.launch {
                it.value?.onDeviceUnlocked()
            }
        }
    }
}
