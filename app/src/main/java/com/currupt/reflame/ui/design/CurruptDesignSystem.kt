package com.currupt.reflame.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.currupt.reflame.account.model.UserEntitlement

object CurruptColors {
    val BackgroundBlack = Color(0xFF080808)
    val SurfaceDark = Color(0xFF121212)
    val CardSurface = Color(0xFF1A1A1A)
    val CardSurfaceElevated = Color(0xFF222222)
    
    val BorderSubtle = Color(0xFF2A2A2A)
    val BorderBright = Color(0xFF404040)
    
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA0A0A0)
    val TextMuted = Color(0xFF666666)
    
    val AccentRed = Color(0xFFE50914)
    val AccentGold = Color(0xFFD4AF37)
    val AccentGoldGlow = Color(0xFFFFD700)
    
    val StatusGreen = Color(0xFF22C55E)
    val StatusYellow = Color(0xFFEAB308)
    val StatusRed = Color(0xFFEF4444)
    val StatusBlue = Color(0xFF3B82F6)

    fun getAccentForEntitlement(entitlement: UserEntitlement): Color {
        return if (entitlement == UserEntitlement.PREMIUM) AccentGold else TextPrimary
    }
}

object CurruptDimensions {
    val PaddingSmall: Dp = 8.dp
    val PaddingMedium: Dp = 16.dp
    val PaddingLarge: Dp = 24.dp
    
    val RadiusSmall: Dp = 8.dp
    val RadiusMedium: Dp = 16.dp
    val RadiusLarge: Dp = 24.dp
    
    val CardElevation: Dp = 4.dp
}

object CurruptTypography {
    val BrandHeader = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        letterSpacing = 3.sp,
        color = CurruptColors.TextPrimary
    )
    
    val Subtitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        color = CurruptColors.TextSecondary
    )
    
    val SectionHeader = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 1.sp,
        color = CurruptColors.TextPrimary
    )
    
    val CardTitle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        color = CurruptColors.TextPrimary
    )
    
    val Body = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = CurruptColors.TextSecondary
    )
    
    val Caption = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = CurruptColors.TextMuted
    )
}
