package com.example.jettrivia.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    val LightGray = Color(0xffb4b9cd)
    val Black = Color(0xff060814)
    val White = Color(0xffeceeef)
    val LightYellow = Color(0xFFC5B845)
    val DarkYellow = Color(0xFFB4A205)
    val Yellow = Color(0xFFFFF06C)
    val gradient = Brush.linearGradient(listOf(Color(0xFFA5F950),
        Color(0xFF2DAC48)
    ))
    val gradient2 = Brush.verticalGradient(
        0.0f to Black,
        5.0f to White,
        startY = 0.0f,
        endY = 10500.0f
    )

}