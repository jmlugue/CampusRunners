package com.malayanquest.app.ui.screens.message

import androidx.compose.runtime.Composable
import com.malayanquest.app.navigation.Screen
import com.malayanquest.app.ui.components.*

@Composable
fun MessageHubScreen(
    onNavigate: (Screen) -> Unit,
    onNavigateBack: () -> Unit
) {
    TabStrip(
        tabs = listOf("All", "Errands", "System"),
        selected = "All",
        onSelected = {}
    )
    InfoPanel("Messaging is connected to a specific errand and is only visible to the requester and selected helper.")
    MessageRouteCard("My Posted Errand Chats", "Open chats for errands where you selected a helper.", "Requester") {
        onNavigate(Screen.MyPosted)
    }
    MessageRouteCard("My Helper Errand Chats", "Open chats for errands assigned to you as helper.", "Helper") {
        onNavigate(Screen.MyHelper)
    }
    CampusButton("Back", primary = false) { onNavigateBack() }
}
