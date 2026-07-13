package com.malayanquest.app.ui.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malayanquest.app.R
import com.malayanquest.app.ui.components.Badge
import com.malayanquest.app.ui.theme.PrimaryBlue
import com.malayanquest.app.ui.theme.SecondaryBlue

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(660.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(PrimaryBlue, SecondaryBlue))),
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 12.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(painterResource(R.drawable.malayan_quest_logo), contentDescription = "Malayan Quest", modifier = Modifier.size(82.dp))
            }
            Text("Malayan Quest", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("Campus errands, made easier.", color = Color(0xFFEAF3FF), fontSize = 16.sp, textAlign = TextAlign.Center)
            Badge("MCL student service app", Color.White, fill = Color.White.copy(alpha = 0.14f), border = Color.White)
        }
        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 42.dp, vertical = 42.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.25f)
        )
    }
}
