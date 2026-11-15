package com.parzij.recurra.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.parzij.recurra.R

@OptIn(ExperimentalTextApi::class)
val OnestFontFamily: FontFamily =
    FontFamily(
        listOf(
                FontWeight.Thin,
                FontWeight.ExtraLight,
                FontWeight.Light,
                FontWeight.Normal,
                FontWeight.Medium,
                FontWeight.SemiBold,
                FontWeight.Bold,
                FontWeight.ExtraBold,
                FontWeight.Black,
            )
            .map { weight ->
                Font(
                    resId = R.font.onest_variable,
                    weight = weight,
                    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
                )
            }
    )

val Typography: Typography =
    Typography().let { defaults ->
        Typography(
            displayLarge = defaults.displayLarge.copy(fontFamily = OnestFontFamily),
            displayMedium = defaults.displayMedium.copy(fontFamily = OnestFontFamily),
            displaySmall = defaults.displaySmall.copy(fontFamily = OnestFontFamily),
            headlineLarge = defaults.headlineLarge.copy(fontFamily = OnestFontFamily),
            headlineMedium = defaults.headlineMedium.copy(fontFamily = OnestFontFamily),
            headlineSmall = defaults.headlineSmall.copy(fontFamily = OnestFontFamily),
            titleLarge = defaults.titleLarge.copy(fontFamily = OnestFontFamily),
            titleMedium = defaults.titleMedium.copy(fontFamily = OnestFontFamily),
            titleSmall = defaults.titleSmall.copy(fontFamily = OnestFontFamily),
            bodyLarge = defaults.bodyLarge.copy(fontFamily = OnestFontFamily),
            bodyMedium = defaults.bodyMedium.copy(fontFamily = OnestFontFamily),
            bodySmall = defaults.bodySmall.copy(fontFamily = OnestFontFamily),
            labelLarge = defaults.labelLarge.copy(fontFamily = OnestFontFamily),
            labelMedium = defaults.labelMedium.copy(fontFamily = OnestFontFamily),
            labelSmall = defaults.labelSmall.copy(fontFamily = OnestFontFamily),
        )
    }
