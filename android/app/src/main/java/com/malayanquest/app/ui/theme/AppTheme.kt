package com.malayanquest.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF172852)
val SecondaryBlue = Color(0xFF263F70)

val BrandRed = Color(0xFFEC1F28)
val BrandRedSoft = Color(0xFFFFF1F2)

val BackgroundSoft = Color(0xFFF5F7FA)
val CardLight = Color(0xFFF7F9FC)
val BorderSoft = Color(0xFFD9DFE8)

val TextPrimary = Color(0xFF172852)
val TextSecondary = Color(0xFF667085)

val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFF59E0B)
val DangerRed = Color(0xFFB3261E)
@Composable
fun CampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PrimaryBlue,
            secondary = SecondaryBlue,
            tertiary = BrandRed,
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
