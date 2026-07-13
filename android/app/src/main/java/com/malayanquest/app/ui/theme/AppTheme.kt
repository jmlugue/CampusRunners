package com.malayanquest.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF0B4EA2)
val SecondaryBlue = Color(0xFF1976D2)
val BackgroundSoft = Color(0xFFEEF5FB)
val CardLight = Color(0xFFF4F8FC)
val BorderSoft = Color(0xFFD7E6F5)
val TextPrimary = Color(0xFF102A43)
val TextSecondary = Color(0xFF6B7280)
val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFF9A825)
val DangerRed = Color(0xFFD32F2F)

@Composable
fun CampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PrimaryBlue,
            secondary = SecondaryBlue,
            background = BackgroundSoft,
            surface = Color.White,
            error = DangerRed
        ),
        content = content
    )
}

fun colorForStatus(status: String?): Color {
    if (status == null) return PrimaryBlue
    return when {
        status.contains("Completed") || status.contains("Confirmed") || status.contains("Rated") || status.contains("Closed") -> SuccessGreen
        status.contains("Cancel") || status.contains("Reported") || status.contains("Removed") -> DangerRed
        status.contains("Flagged") || status.contains("pending") || status.contains("Has Applicants") -> WarningAmber
        else -> PrimaryBlue
    }
}
