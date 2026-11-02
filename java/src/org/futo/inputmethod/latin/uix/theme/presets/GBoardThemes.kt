package org.futo.inputmethod.latin.uix.theme.presets

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.theme.ThemeOption

// GBoard family: light grey-blue board, white keys, grey function keys,
// black text and a coloured enter key — plus colour variants.

val GBoardDefault = ThemeOption(
    dynamic = false,
    key = "GBoardDefault",
    name = R.string.theme_gboard_default,
    available = { true }
) {
    lightColorScheme(
        primary = Color(0xFF0B57D0),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFD3E3FD),
        onPrimaryContainer = Color(0xFF041E49),
        secondary = Color(0xFF3C4043),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFD9DEE5),
        onSecondaryContainer = Color(0xFF1F1F1F),
        tertiary = Color(0xFF5F6368),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFE7EAED),
        onBackground = Color(0xFF1F1F1F),
        surface = Color(0xFFE7EAED),
        onSurface = Color(0xFF1F1F1F),
        surfaceVariant = Color(0xFFD9DEE5),
        onSurfaceVariant = Color(0xFF3C4043),
        outline = Color(0xFF9AA0A6),
        outlineVariant = Color(0xFFC4C7C5)
    )
}

val GBoardDark = ThemeOption(
    dynamic = false,
    key = "GBoardDark",
    name = R.string.theme_gboard_dark,
    available = { true }
) {
    darkColorScheme(
        primary = Color(0xFFA8C7FA),
        onPrimary = Color(0xFF041E49),
        primaryContainer = Color(0xFF004A77),
        onPrimaryContainer = Color(0xFFC2E7FF),
        secondary = Color(0xFFC4C7C5),
        onSecondary = Color(0xFF1B1B1D),
        secondaryContainer = Color(0xFF303134),
        onSecondaryContainer = Color(0xFFE3E3E3),
        tertiary = Color(0xFF7A7E83),
        onTertiary = Color(0xFF1B1B1D),
        background = Color(0xFF1B1B1D),
        onBackground = Color(0xFFE3E3E3),
        surface = Color(0xFF1B1B1D),
        onSurface = Color(0xFFE3E3E3),
        surfaceVariant = Color(0xFF303134),
        onSurfaceVariant = Color(0xFFC4C7C5),
        outline = Color(0xFF5F6368),
        outlineVariant = Color(0xFF444746)
    )
}

val GBoardBlue = ThemeOption(
    dynamic = false,
    key = "GBoardBlue",
    name = R.string.theme_gboard_blue,
    available = { true }
) {
    lightColorScheme(
        primary = Color(0xFF0B57D0),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFA8C7FA),
        onPrimaryContainer = Color(0xFF041E49),
        secondary = Color(0xFF0842A0),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFBED5FB),
        onSecondaryContainer = Color(0xFF041E49),
        tertiary = Color(0xFF00639B),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFD3E3FD),
        onBackground = Color(0xFF041E49),
        surface = Color(0xFFD3E3FD),
        onSurface = Color(0xFF041E49),
        surfaceVariant = Color(0xFFA8C7FA),
        onSurfaceVariant = Color(0xFF041E49),
        outline = Color(0xFF5B7DB1),
        outlineVariant = Color(0xFFA8C7FA)
    )
}

val GBoardGreen = ThemeOption(
    dynamic = false,
    key = "GBoardGreen",
    name = R.string.theme_gboard_green,
    available = { true }
) {
    lightColorScheme(
        primary = Color(0xFF188038),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFA6DAB5),
        onPrimaryContainer = Color(0xFF052E16),
        secondary = Color(0xFF0D6526),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFA6DAB5),
        onSecondaryContainer = Color(0xFF052E16),
        tertiary = Color(0xFF146C2E),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFCEEAD6),
        onBackground = Color(0xFF052E16),
        surface = Color(0xFFCEEAD6),
        onSurface = Color(0xFF052E16),
        surfaceVariant = Color(0xFFA6DAB5),
        onSurfaceVariant = Color(0xFF0A3818),
        outline = Color(0xFF5F8A6B),
        outlineVariant = Color(0xFFA6DAB5)
    )
}

val GBoardYellow = ThemeOption(
    dynamic = false,
    key = "GBoardYellow",
    name = R.string.theme_gboard_yellow,
    available = { true }
) {
    lightColorScheme(
        primary = Color(0xFFE37400),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFDE293),
        onPrimaryContainer = Color(0xFF3F3003),
        secondary = Color(0xFF7A4A00),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFDE293),
        onSecondaryContainer = Color(0xFF3F3003),
        tertiary = Color(0xFFB06000),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFFEF7E0),
        onBackground = Color(0xFF3F3003),
        surface = Color(0xFFFEF7E0),
        onSurface = Color(0xFF3F3003),
        surfaceVariant = Color(0xFFFDE293),
        onSurfaceVariant = Color(0xFF5C3D00),
        outline = Color(0xFFA68A3C),
        outlineVariant = Color(0xFFFDE293)
    )
}

val GBoardPink = ThemeOption(
    dynamic = false,
    key = "GBoardPink",
    name = R.string.theme_gboard_pink,
    available = { true }
) {
    lightColorScheme(
        primary = Color(0xFFC2185B),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFF8BBD0),
        onPrimaryContainer = Color(0xFF3E0A1E),
        secondary = Color(0xFF880E4F),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF8BBD0),
        onSecondaryContainer = Color(0xFF3E0A1E),
        tertiary = Color(0xFFAD1457),
        onTertiary = Color(0xFFFFFFFF),
        background = Color(0xFFFCE4EC),
        onBackground = Color(0xFF3E0A1E),
        surface = Color(0xFFFCE4EC),
        onSurface = Color(0xFF3E0A1E),
        surfaceVariant = Color(0xFFF8BBD0),
        onSurfaceVariant = Color(0xFF5C1031),
        outline = Color(0xFFB06A85),
        outlineVariant = Color(0xFFF8BBD0)
    )
}
