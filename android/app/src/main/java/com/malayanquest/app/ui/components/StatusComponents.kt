package com.malayanquest.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.ui.theme.*
import com.malayanquest.app.util.statusSteps

@Composable
fun StatusTracker(currentStatus: String) {
    val currentIndex = statusSteps.indexOf(currentStatus)
    CampusCard {
        statusSteps.forEachIndexed { index, step ->
            val color = when {
                currentIndex >= 0 && index < currentIndex -> SuccessGreen
                index == currentIndex -> PrimaryBlue
                else -> TextSecondary
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (index <= currentIndex && currentIndex >= 0) color else Color.White)
                            .border(2.dp, color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < currentIndex) {
                            Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (index < statusSteps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(22.dp)
                                .background(if (index < currentIndex) SuccessGreen else BorderSoft)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(step, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(if (index <= currentIndex && currentIndex >= 0) "Updated in current workflow" else "Pending", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
        InfoPanel("You will be notified at every update.", Icons.Filled.Info, "Status updates")
    }
}
