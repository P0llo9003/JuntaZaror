package com.duoc.juntazaror.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.duoc.juntazaror.R

val Poppins = FontFamily(
    Font(R.font.poppinsregular,  FontWeight.Normal),
    Font(R.font.poppinsmedium,   FontWeight.Medium),
    Font(R.font.poppinssemibold, FontWeight.SemiBold),
    Font(R.font.poppinsbold,     FontWeight.Bold)
)

private val base = Typography()

val Typography = Typography(
    displayLarge   = base.displayLarge.copy(fontFamily = Poppins),
    displayMedium  = base.displayMedium.copy(fontFamily = Poppins),
    displaySmall   = base.displaySmall.copy(fontFamily = Poppins),

    headlineLarge  = base.headlineLarge.copy(fontFamily = Poppins),
    headlineMedium = base.headlineMedium.copy(fontFamily = Poppins),
    headlineSmall  = base.headlineSmall.copy(fontFamily = Poppins),

    titleLarge     = base.titleLarge.copy(fontFamily = Poppins),
    titleMedium    = base.titleMedium.copy(fontFamily = Poppins),
    titleSmall     = base.titleSmall.copy(fontFamily = Poppins),

    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium     = base.bodyMedium.copy(fontFamily = Poppins),
    bodySmall      = base.bodySmall.copy(fontFamily = Poppins),

    labelLarge     = base.labelLarge.copy(fontFamily = Poppins),
    labelMedium    = base.labelMedium.copy(fontFamily = Poppins),
    labelSmall     = base.labelSmall.copy(fontFamily = Poppins)
)