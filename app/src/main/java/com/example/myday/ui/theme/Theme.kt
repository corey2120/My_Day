package com.example.myday.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val lightSchemeGreen = lightColorScheme(
    primary = primaryLightGreen,
    onPrimary = onPrimaryLightGreen,
    primaryContainer = primaryContainerLightGreen,
    onPrimaryContainer = onPrimaryContainerLightGreen,
    secondary = secondaryLightGreen,
    onSecondary = onSecondaryLightGreen,
    secondaryContainer = secondaryContainerLightGreen,
    onSecondaryContainer = onSecondaryContainerLightGreen,
    tertiary = tertiaryLightGreen,
    onTertiary = onTertiaryLightGreen,
    tertiaryContainer = tertiaryContainerLightGreen,
    onTertiaryContainer = onTertiaryContainerLightGreen,
    background = backgroundLightGreen,
    onBackground = onBackgroundLightGreen,
    surface = surfaceLightGreen,
    onSurface = onSurfaceLightGreen,
)

private val darkSchemeGreen = darkColorScheme(
    primary = primaryDarkGreen,
    onPrimary = onPrimaryDarkGreen,
    primaryContainer = primaryContainerDarkGreen,
    onPrimaryContainer = onPrimaryContainerDarkGreen,
    secondary = secondaryDarkGreen,
    onSecondary = onSecondaryDarkGreen,
    secondaryContainer = secondaryContainerDarkGreen,
    onSecondaryContainer = onSecondaryContainerDarkGreen,
    tertiary = tertiaryDarkGreen,
    onTertiary = onTertiaryDarkGreen,
    tertiaryContainer = tertiaryContainerDarkGreen,
    onTertiaryContainer = onTertiaryContainerDarkGreen,
    background = backgroundDarkGreen,
    onBackground = onBackgroundDarkGreen,
    surface = surfaceDarkGreen,
    onSurface = onSurfaceDarkGreen,
)

private val lightSchemePurple = lightColorScheme(
    primary = primaryLightPurple,
    onPrimary = onPrimaryLightPurple,
    primaryContainer = primaryContainerLightPurple,
    onPrimaryContainer = onPrimaryContainerLightPurple,
    secondary = secondaryLightPurple,
    onSecondary = onSecondaryLightPurple,
    secondaryContainer = secondaryContainerLightPurple,
    onSecondaryContainer = onSecondaryContainerLightPurple,
    tertiary = tertiaryLightPurple,
    onTertiary = onTertiaryLightPurple,
    tertiaryContainer = tertiaryContainerLightPurple,
    onTertiaryContainer = onTertiaryContainerLightPurple,
    background = backgroundLightPurple,
    onBackground = onBackgroundLightPurple,
    surface = surfaceLightPurple,
    onSurface = onSurfaceLightPurple,
)

private val darkSchemePurple = darkColorScheme(
    primary = primaryDarkPurple,
    onPrimary = onPrimaryDarkPurple,
    primaryContainer = primaryContainerDarkPurple,
    onPrimaryContainer = onPrimaryContainerDarkPurple,
    secondary = secondaryDarkPurple,
    onSecondary = onSecondaryDarkPurple,
    secondaryContainer = secondaryContainerDarkPurple,
    onSecondaryContainer = onSecondaryContainerDarkPurple,
    tertiary = tertiaryDarkPurple,
    onTertiary = onTertiaryDarkPurple,
    tertiaryContainer = tertiaryContainerDarkPurple,
    onTertiaryContainer = onTertiaryContainerDarkPurple,
    background = backgroundDarkPurple,
    onBackground = onBackgroundDarkPurple,
    surface = surfaceDarkPurple,
    onSurface = onSurfaceDarkPurple,
)

private val lightSchemeOcean = lightColorScheme(
    primary = primaryLightOcean,
    onPrimary = onPrimaryLightOcean,
    primaryContainer = primaryContainerLightOcean,
    onPrimaryContainer = onPrimaryContainerLightOcean,
    secondary = secondaryLightOcean,
    onSecondary = onSecondaryLightOcean,
    secondaryContainer = secondaryContainerLightOcean,
    onSecondaryContainer = onSecondaryContainerLightOcean,
    tertiary = tertiaryLightOcean,
    onTertiary = onTertiaryLightOcean,
    tertiaryContainer = tertiaryContainerLightOcean,
    onTertiaryContainer = onTertiaryContainerLightOcean,
    background = backgroundLightOcean,
    onBackground = onBackgroundLightOcean,
    surface = surfaceLightOcean,
    onSurface = onSurfaceLightOcean,
)

private val darkSchemeOcean = darkColorScheme(
    primary = primaryDarkOcean,
    onPrimary = onPrimaryDarkOcean,
    primaryContainer = primaryContainerDarkOcean,
    onPrimaryContainer = onPrimaryContainerDarkOcean,
    secondary = secondaryDarkOcean,
    onSecondary = onSecondaryDarkOcean,
    secondaryContainer = secondaryContainerDarkOcean,
    onSecondaryContainer = onSecondaryContainerDarkOcean,
    tertiary = tertiaryDarkOcean,
    onTertiary = onTertiaryDarkOcean,
    tertiaryContainer = tertiaryContainerDarkOcean,
    onTertiaryContainer = onTertiaryContainerDarkOcean,
    background = backgroundDarkOcean,
    onBackground = onBackgroundDarkOcean,
    surface = surfaceDarkOcean,
    onSurface = onSurfaceDarkOcean,
)

private val lightSchemeSunset = lightColorScheme(
    primary = primaryLightSunset,
    onPrimary = onPrimaryLightSunset,
    primaryContainer = primaryContainerLightSunset,
    onPrimaryContainer = onPrimaryContainerLightSunset,
    secondary = secondaryLightSunset,
    onSecondary = onSecondaryLightSunset,
    secondaryContainer = secondaryContainerLightSunset,
    onSecondaryContainer = onSecondaryContainerLightSunset,
    tertiary = tertiaryLightSunset,
    onTertiary = onTertiaryLightSunset,
    tertiaryContainer = tertiaryContainerLightSunset,
    onTertiaryContainer = onTertiaryContainerLightSunset,
    background = backgroundLightSunset,
    onBackground = onBackgroundLightSunset,
    surface = surfaceLightSunset,
    onSurface = onSurfaceLightSunset,
)

private val darkSchemeSunset = darkColorScheme(
    primary = primaryDarkSunset,
    onPrimary = onPrimaryDarkSunset,
    primaryContainer = primaryContainerDarkSunset,
    onPrimaryContainer = onPrimaryContainerDarkSunset,
    secondary = secondaryDarkSunset,
    onSecondary = onSecondaryDarkSunset,
    secondaryContainer = secondaryContainerDarkSunset,
    onSecondaryContainer = onSecondaryContainerDarkSunset,
    tertiary = tertiaryDarkSunset,
    onTertiary = onTertiaryDarkSunset,
    tertiaryContainer = tertiaryContainerDarkSunset,
    onTertiaryContainer = onTertiaryContainerDarkSunset,
    background = backgroundDarkSunset,
    onBackground = onBackgroundDarkSunset,
    surface = surfaceDarkSunset,
    onSurface = onSurfaceDarkSunset,
)

private val lightSchemeRose = lightColorScheme(
    primary = primaryLightRose,
    onPrimary = onPrimaryLightRose,
    primaryContainer = primaryContainerLightRose,
    onPrimaryContainer = onPrimaryContainerLightRose,
    secondary = secondaryLightRose,
    onSecondary = onSecondaryLightRose,
    secondaryContainer = secondaryContainerLightRose,
    onSecondaryContainer = onSecondaryContainerLightRose,
    tertiary = tertiaryLightRose,
    onTertiary = onTertiaryLightRose,
    tertiaryContainer = tertiaryContainerLightRose,
    onTertiaryContainer = onTertiaryContainerLightRose,
    background = backgroundLightRose,
    onBackground = onBackgroundLightRose,
    surface = surfaceLightRose,
    onSurface = onSurfaceLightRose,
)

private val darkSchemeRose = darkColorScheme(
    primary = primaryDarkRose,
    onPrimary = onPrimaryDarkRose,
    primaryContainer = primaryContainerDarkRose,
    onPrimaryContainer = onPrimaryContainerDarkRose,
    secondary = secondaryDarkRose,
    onSecondary = onSecondaryDarkRose,
    secondaryContainer = secondaryContainerDarkRose,
    onSecondaryContainer = onSecondaryContainerDarkRose,
    tertiary = tertiaryDarkRose,
    onTertiary = onTertiaryDarkRose,
    tertiaryContainer = tertiaryContainerDarkRose,
    onTertiaryContainer = onTertiaryContainerDarkRose,
    background = backgroundDarkRose,
    onBackground = onBackgroundDarkRose,
    surface = surfaceDarkRose,
    onSurface = onSurfaceDarkRose,
)

private val lightSchemeTeal = lightColorScheme(
    primary = primaryLightTeal,
    onPrimary = onPrimaryLightTeal,
    primaryContainer = primaryContainerLightTeal,
    onPrimaryContainer = onPrimaryContainerLightTeal,
    secondary = secondaryLightTeal,
    onSecondary = onSecondaryLightTeal,
    secondaryContainer = secondaryContainerLightTeal,
    onSecondaryContainer = onSecondaryContainerLightTeal,
    tertiary = tertiaryLightTeal,
    onTertiary = onTertiaryLightTeal,
    tertiaryContainer = tertiaryContainerLightTeal,
    onTertiaryContainer = onTertiaryContainerLightTeal,
    background = backgroundLightTeal,
    onBackground = onBackgroundLightTeal,
    surface = surfaceLightTeal,
    onSurface = onSurfaceLightTeal,
)

private val darkSchemeTeal = darkColorScheme(
    primary = primaryDarkTeal,
    onPrimary = onPrimaryDarkTeal,
    primaryContainer = primaryContainerDarkTeal,
    onPrimaryContainer = onPrimaryContainerDarkTeal,
    secondary = secondaryDarkTeal,
    onSecondary = onSecondaryDarkTeal,
    secondaryContainer = secondaryContainerDarkTeal,
    onSecondaryContainer = onSecondaryContainerDarkTeal,
    tertiary = tertiaryDarkTeal,
    onTertiary = onTertiaryDarkTeal,
    tertiaryContainer = tertiaryContainerDarkTeal,
    onTertiaryContainer = onTertiaryContainerDarkTeal,
    background = backgroundDarkTeal,
    onBackground = onBackgroundDarkTeal,
    surface = surfaceDarkTeal,
    onSurface = onSurfaceDarkTeal,
)

private val lightSchemeMidnight = lightColorScheme(
    primary = primaryLightMidnight,
    onPrimary = onPrimaryLightMidnight,
    primaryContainer = primaryContainerLightMidnight,
    onPrimaryContainer = onPrimaryContainerLightMidnight,
    secondary = secondaryLightMidnight,
    onSecondary = onSecondaryLightMidnight,
    secondaryContainer = secondaryContainerLightMidnight,
    onSecondaryContainer = onSecondaryContainerLightMidnight,
    tertiary = tertiaryLightMidnight,
    onTertiary = onTertiaryLightMidnight,
    tertiaryContainer = tertiaryContainerLightMidnight,
    onTertiaryContainer = onTertiaryContainerLightMidnight,
    background = backgroundLightMidnight,
    onBackground = onBackgroundLightMidnight,
    surface = surfaceLightMidnight,
    onSurface = onSurfaceLightMidnight,
)

private val darkSchemeMidnight = darkColorScheme(
    primary = primaryDarkMidnight,
    onPrimary = onPrimaryDarkMidnight,
    primaryContainer = primaryContainerDarkMidnight,
    onPrimaryContainer = onPrimaryContainerDarkMidnight,
    secondary = secondaryDarkMidnight,
    onSecondary = onSecondaryDarkMidnight,
    secondaryContainer = secondaryContainerDarkMidnight,
    onSecondaryContainer = onSecondaryContainerDarkMidnight,
    tertiary = tertiaryDarkMidnight,
    onTertiary = onTertiaryDarkMidnight,
    tertiaryContainer = tertiaryContainerDarkMidnight,
    onTertiaryContainer = onTertiaryContainerDarkMidnight,
    background = backgroundDarkMidnight,
    onBackground = onBackgroundDarkMidnight,
    surface = surfaceDarkMidnight,
    onSurface = onSurfaceDarkMidnight,
)

@Composable
fun MyDayTheme(
    themeName: String = "Default Blue",
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> when (themeName) {
            "Forest Green" -> darkSchemeGreen
            "Deep Purple" -> darkSchemePurple
            "Ocean Blue" -> darkSchemeOcean
            "Sunset Orange" -> darkSchemeSunset
            "Rose Pink" -> darkSchemeRose
            "Teal Mint" -> darkSchemeTeal
            "Midnight Dark" -> darkSchemeMidnight
            else -> darkScheme
        }
        else -> when (themeName) {
            "Forest Green" -> lightSchemeGreen
            "Deep Purple" -> lightSchemePurple
            "Ocean Blue" -> lightSchemeOcean
            "Sunset Orange" -> lightSchemeSunset
            "Rose Pink" -> lightSchemeRose
            "Teal Mint" -> lightSchemeTeal
            "Midnight Dark" -> lightSchemeMidnight
            else -> lightScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
