package com.duoc.juntazaror.ui.theme

/*sin uso por ahora: import androidx.compose.foundation.isSystemInDarkTheme*/
/*sin uso por ahora: import androidx.compose.ui.platform.LocalContext*/
/*sin uso por ahora: import androidx.compose.material3.darkColorScheme*/
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val lightColorScheme = lightColorScheme(
    primary = Tierra,
    onPrimary = Latte,
    primaryContainer = Arena,
    onPrimaryContainer = Midnight,

    secondary = Oliva,
    onSecondary = Arena,
    secondaryContainer = Oliva,
    onSecondaryContainer = TextoOscuro,

    tertiary = Latte,
    onTertiary = Midnight,

    background = Linen,
    onBackground = TextoOscuro,

    surface = Linen,
    onSurface = TextoOscuro,
    surfaceVariant = Arena,
    onSurfaceVariant = TextoOscuro,

    outline = Oliva,

    error = Rojo,
    onError = Linen
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme,
        typography = Typography,
        content = content
    )
}