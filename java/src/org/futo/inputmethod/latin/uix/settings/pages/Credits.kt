package org.futo.inputmethod.latin.uix.settings.pages

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.futo.inputmethod.latin.uix.settings.ScreenTitle
import org.futo.inputmethod.latin.uix.settings.ScrollableList
import org.futo.inputmethod.latin.uix.openURI

@Preview(showBackground = true)
@Composable
fun CreditsScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    ScrollableList {
        ScreenTitle("Leaf Board", showBack = true, navController)

        ParagraphText("Leaf Board is a privacy-first Android keyboard with a clean, light design in white, black and brown. Your typing stays on your device: no accounts, no analytics, no ads. The only features that use the network are the ones you explicitly tap — Translate and system voice typing.")

        ScreenTitle("Open Source Foundations")

        ParagraphText("This keyboard is based on the LatinIME keyboard from the Android Open Source Project. Thank you to the original AOSP LatinIME Keyboard developers. The LatinIME keyboard is Apache-2.0 licensed and Copyright (C) 2011 The Android Open Source Project",
            modifier = Modifier.clickable {
                context.openURI("https://android.googlesource.com/platform/packages/inputmethods/LatinIME/")
            })

        ParagraphText("Leaf Board builds on the open-source FUTO Keyboard project, which is Apache-2.0 licensed. Thank you to the FUTO team and contributors for the foundation.",
            modifier = Modifier.clickable {
                context.openURI("https://github.com/futo-org/android-keyboard")
            })

        ParagraphText("Bengali phonetic typing uses the open-source Ridmik Parser by Shamim Hasnath. Thank you for making Bengali typing on phones easier.")

        ParagraphText("The keyboard font is Inter, licensed under the SIL Open Font License 1.1.",
            modifier = Modifier.clickable {
                context.openURI("https://fonts.google.com/specimen/Inter")
            })

        ParagraphText("Thank you to Feather Icons for providing many of the icons seen in this app. Feather Icons is MIT-licensed. Copyright (c) 2013-2017 Cole Bemis",
            modifier = Modifier.clickable {
                context.openURI("https://feathericons.com")
            })

        ParagraphText("Emoji icons are taken from the Noto Emoji font, licensed under OFL.",
            modifier = Modifier.clickable {
                context.openURI("https://fonts.google.com/noto/specimen/Noto+Emoji")
            })

        ParagraphText("Actions rearrangement menu uses the Reorderable library made by Calvin Liang, licensed under Apache-2.0. Reorderable is Copyright (c) 2023 Calvin Liang",
            modifier = Modifier.clickable {
                context.openURI("https://github.com/Calvin-LL/Reorderable")
            })
    }
}
