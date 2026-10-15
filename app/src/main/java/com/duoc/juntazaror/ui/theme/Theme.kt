package com.duoc.juntazaror.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val lightColorScheme = lightColorScheme(
    primary = AzulRey,
    onPrimary = Blanco,
    primaryContainer = AzulReyClaro,
    onPrimaryContainer = AzulReyOscuro,

    secondary = AzulReyOscuro,
    onSecondary = Blanco,
    secondaryContainer = GrisClaroMedio,
    onSecondaryContainer = TextoOscuro,

    tertiary = AzulReyOscuro,
    onTertiary = Blanco,

    background = GrisClaro,
    onBackground = TextoOscuro,

    surface = Blanco,
    onSurface = TextoOscuro,
    surfaceVariant = GrisClaroMedio,
    onSurfaceVariant = TextoOscuro,

    outline = GrisBorde,

    error = Rojo,
    onError = Blanco
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme,
        typography = Typography,
        content = content
    )
}