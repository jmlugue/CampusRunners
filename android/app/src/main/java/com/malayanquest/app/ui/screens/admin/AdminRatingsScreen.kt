package com.malayanquest.app.ui.screens.admin

import androidx.compose.runtime.Composable
import com.malayanquest.app.ui.components.*

@Composable
fun AdminRatingsScreen(
    onOpenErrandRecords: () -> Unit,
    onNavigateBack: () -> Unit
) {
    InfoPanel("This prototype keeps user ratings available through profile and history records. Add a backend endpoint for all ratings if the admin needs a full feedback export.")
    CampusButton("View Errand Records") { onOpenErrandRecords() }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
