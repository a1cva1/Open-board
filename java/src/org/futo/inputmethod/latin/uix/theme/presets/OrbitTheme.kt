package org.futo.inputmethod.latin.uix.theme.presets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.theme.ThemeOption

// Leaf Board palette: white, black, brown. Nothing else.
val LeafBrown = Color(0xFFB4652F)
val LeafBrownDark = Color(0xFF8A4F1F)
val LeafLightBrown = Color(0xFFF1E4D3)
val LeafWhite = Color(0xFFFFFFFF)
val LeafBlack = Color(0xFF121212)

// Legacy names kept so the theme preview code keeps working;
// they now resolve to the Leaf palette.
val OrbitMatteYellow = LeafLightBrown
val OrbitMatteBlue = LeafLightBrown
val OrbitMatteRed = LeafBrown
val OrbitMatteWhite = LeafWhite
val OrbitMatteDark = LeafBrown
val OrbitBlack = LeafWhite

private val orbitColorScheme = lightColorScheme(
    primary = LeafBrown,
    onPrimary = LeafWhite,
    primaryContainer = LeafLightBrown,
    onPrimaryContainer = LeafBlack,
    secondary = LeafBrown,
    onSecondary = LeafWhite,
    secondaryContainer = LeafLightBrown,
    onSecondaryContainer = LeafBlack,
    tertiary = LeafBrownDark,
    onTertiary = LeafWhite,
    background = LeafWhite,
    onBackground = LeafBlack,
    surface = LeafWhite,
    onSurface = LeafBlack,
    surfaceVariant = LeafLightBrown,
    onSurfaceVariant = LeafBlack,
    outline = LeafBrown,
    outlineVariant = Color(0xFFD9CFC2)
)

val OrbitTheme = ThemeOption(
    dynamic = false,
    key = "OrbitTheme",
    name = R.string.theme_orbit,
    available = { true }
) {
    orbitColorScheme
}

@Composable
fun OrbitPreviewKeyboard() {
    val yellow = OrbitMatteYellow
    val blue = OrbitMatteBlue
    val dark = OrbitMatteDark
    val white = OrbitMatteWhite
    val red = OrbitMatteRed

    val row1 = listOf(white, white, white, white, white, white, white, white, white, white)
    val row2 = listOf(white, white, white, white, white, white, white, white, white)
    val row3 = listOf(yellow, white, white, white, white, white, white, white, blue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp, Alignment.CenterHorizontally)
        ) {
            row1.forEach { color ->
                Surface(
                    modifier = Modifier.weight(1f).height(10.dp),
                    color = color,
                    shape = RoundedCornerShape(2.dp)
                ) {}
            }
        }
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp, Alignment.CenterHorizontally)
        ) {
            row2.forEach { color ->
                Surface(
                    modifier = Modifier.weight(1f).height(10.dp),
                    color = color,
                    shape = RoundedCornerShape(2.dp)
                ) {}
            }
        }
        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp, Alignment.CenterHorizontally)
        ) {
            row3.forEach { color ->
                Surface(
                    modifier = Modifier.weight(1f).height(10.dp),
                    color = color,
                    shape = RoundedCornerShape(2.dp)
                ) {}
            }
        }
        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(1.5.dp, Alignment.CenterHorizontally)
        ) {
            Surface(modifier = Modifier.weight(1.2f).height(10.dp), color = yellow, shape = RoundedCornerShape(2.dp)) {}
            Surface(modifier = Modifier.weight(0.9f).height(10.dp), color = blue, shape = RoundedCornerShape(2.dp)) {}
            Surface(modifier = Modifier.weight(0.9f).height(10.dp), color = white, shape = RoundedCornerShape(2.dp)) {}
            Surface(modifier = Modifier.weight(3.5f).height(10.dp), color = dark, shape = RoundedCornerShape(2.dp)) {}
            Surface(modifier = Modifier.weight(0.9f).height(10.dp), color = white, shape = RoundedCornerShape(2.dp)) {}
            Surface(modifier = Modifier.weight(1.2f).height(10.dp), color = red, shape = RoundedCornerShape(2.dp)) {}
        }
    }
}
